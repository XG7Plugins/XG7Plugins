package com.xg7plugins.data.database;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.dao.RepositoryManager;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.ConnectorRegistry;
import com.xg7plugins.data.database.connector.SQLConfigs;
import com.xg7plugins.data.database.connector.connectors.MariaDBConnector;
import com.xg7plugins.data.database.connector.connectors.MySQLConnector;
import com.xg7plugins.data.database.connector.connectors.SQLiteConnector;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.data.database.processor.DatabaseProcessor;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages database connections and operations for plugins.
 * Handles different types of SQL databases (SQLite, MySQL, MariaDB),
 * entity caching, and table creation.
 */
@Getter
public class DatabaseManager {

    private final DatabaseProcessor processor = new DatabaseProcessor(this);
    private final RepositoryManager daoManager = new RepositoryManager();
    private final TableCreator tableCreator = new TableCreator();
    private final ConnectorRegistry connectorRegistry;

    private final ObjectCache<String, Entity> cachedEntities;

    /**
     * Retrieves a database connection for the specified plugin.
     *
     * @param plugin The plugin requesting the connection
     * @return The database connection
     * @throws Exception If the connection cannot be established
     */
    public Connection getConnection(Plugin plugin) throws Exception {
        return connectorRegistry.getConnection(plugin);
    }

    /**
     * Initializes the DatabaseManager with cache settings and database connectors.
     *
     * @param plugin The XG7Plugins instance
     */
    public DatabaseManager(XG7Plugins plugin) {

        cachedEntities = new ObjectCache<>(
                plugin,
                ConfigFile.mainConfigOf(plugin).section("sql").getTimeInMilliseconds("cache-expires", 30 * 60 * 1000L),
                false,
                "cached-entities",
                false,
                String.class,
                Entity.class
        );

        connectorRegistry = new ConnectorRegistry();
        connectorRegistry.registerConnector(new SQLiteConnector());
        connectorRegistry.registerConnector(new MySQLConnector());
        connectorRegistry.registerConnector(new MariaDBConnector());

    }

    /**
     * Establishes a database connection for a plugin and creates the necessary tables.
     *
     * @param plugin        The plugin to connect
     * @param entityClasses Entity classes to create tables for
     */
    @SafeVarargs
    public final void connectPlugin(Plugin plugin, Class<? extends Entity>... entityClasses) {

        if (entityClasses == null) return;

        plugin.getDebug().log("Connecting " + plugin.getName() + " to database...");

        ConfigFile pluginConfig = ConfigFile.mainConfigOf(plugin);
        ConfigFile xg7PluginsConfig = ConfigFile.mainConfigOf(XG7Plugins.getInstance());

        if (!pluginConfig.root().contains("sql")) {
            plugin.getDebug().severe("Connection aborted! No sql configs found in config.yml!");
            return;
        }

        SQLConfigs sqlConfigs = SQLConfigs.of(pluginConfig, xg7PluginsConfig);

        plugin.getDebug().info("database", "Connection type: " + sqlConfigs.getConnectionType());

        Connector connector = connectorRegistry.getConnector(sqlConfigs.getConnectionType());

        if (connector == null) {
            plugin.getDebug().severe("Connection type not found!");
            plugin.getDebug().severe("Connection aborted!");
            return;
        }

        try {
            connector.connect(plugin, sqlConfigs);
        } catch (Exception e) {
            plugin.getDebug().severe("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        plugin.getDebug().log("Successfully connected to database!");

        plugin.getDebug().log("Checking tables...");

        Arrays.stream(entityClasses).forEach(aClass -> tableCreator.createTableOf(plugin, aClass));

        plugin.getDebug().log("Successfully checked tables!");

    }

    public final void registerRepositories(List<Repository<?, ?>> Repositories) {
        if (Repositories == null) return;
        Repositories.forEach(daoManager::registerRepository);
    }

    /**
     * Disconnects a plugin from its database connection.
     *
     * @param plugin The plugin to disconnect
     */
    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getDebug().log("Disconnecting database...");
        connectorRegistry.getConnector(plugin).disconnect(plugin);
        plugin.getDebug().log("Disconnected database!");
    }

    /**
     * Shuts down all database connections and processors.
     * Disconnects all plugins from their database connections.
     *
     * @throws Exception If an error occurs during shutdown
     */
    public void shutdown() throws Exception {
        XG7Plugins.getAPI().getAllXG7Plugins().forEach(plugin -> {
            try {
                disconnectPlugin(plugin);
            } catch (Exception e) {
                plugin.getDebug().severe("Error while disconnecting database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> CompletableFuture<List<T>> getAllCachedEntities(Class<T> entityClass) {
        return CompletableFuture.supplyAsync(() ->
                (List<T>) cachedEntities.asMap().join().values().stream()
                    .filter(entity -> entityClass.isAssignableFrom(entity.getClass()))
                    .collect(Collectors.toList())
        );
    }

    /**
     * Gets a cached entity from the cache using the plugin name and ID as a key.
     *
     * @param plugin The plugin that owns the entity
     * @param id     The unique identifier of the entity
     * @param <T>    The entity type
     * @return CompletableFuture containing the cached entity
     */
    public <T extends Entity> CompletableFuture<T> getCachedEntity(@NotNull Plugin plugin, String id) {
        return (CompletableFuture<T>) cachedEntities.get(plugin.getName() + ":" + id);
    }

    /**
     * Checks if an entity exists in the cache.
     *
     * @param plugin The plugin that owns the entity
     * @param id     The unique identifier to check
     * @return CompletableFuture containing true if the entity is cached, false otherwise
     */
    public CompletableFuture<Boolean> containsCachedEntity(@NotNull Plugin plugin, String id) {
        return cachedEntities.containsKey(plugin.getName() + ":" + id);
    }

    /**
     * Adds an entity to the cache.
     *
     * @param plugin The plugin that owns the entity
     * @param id     The unique identifier for the entity
     * @param entity The entity object to cache
     */
    public void cacheEntity(@NotNull Plugin plugin, String id, Entity entity) {
        cachedEntities.put(plugin.getName() + ":" + id, entity);
    }

    /**
     * Removes an entity from the cache.
     *
     * @param plugin The plugin that owns the entity
     * @param id     The unique identifier of the entity to remove
     */
    public void unCacheEntity(@NotNull Plugin plugin, String id) {
        cachedEntities.remove(plugin.getName() + ":" + id);
    }

    /**
     * Reloads a plugin's database connection by disconnecting and reconnecting.
     *
     * @param plugin The plugin whose connection should be reloaded
     */
    public void reloadConnection(Plugin plugin) {
        plugin.getDebug().log("Reloading database connection...");

        disconnectPlugin(plugin);
        connectPlugin(plugin);

        plugin.getDebug().log("Reloaded database connection!");
    }

}