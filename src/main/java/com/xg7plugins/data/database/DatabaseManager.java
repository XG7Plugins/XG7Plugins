package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.data.dao.DAOManager;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.ConnectorRegistry;
import com.xg7plugins.data.database.connector.SQLConfigs;
import com.xg7plugins.data.database.connector.connectors.MariaDBConnector;
import com.xg7plugins.data.database.connector.connectors.MySQLConnector;
import com.xg7plugins.data.database.connector.connectors.SQLiteConnector;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.managers.Manager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages database connections and operations for plugins.
 * Handles different types of SQL databases (SQLite, MySQL, MariaDB),
 * entity caching, and table creation.
 */
@Getter
public class DatabaseManager implements Manager {

    private final DatabaseProcessor processor = new DatabaseProcessor(this);
    private final DAOManager daoManager = new DAOManager();
    private final TableCreator tableCreator = new TableCreator();
    private final ConnectorRegistry connectorRegistry;

    private final ObjectCache<String, Entity> cachedEntities;

    /**
     * Retrieves a database connection for the specified plugin.
     *
     * @param plugin The plugin requesting the connection
     * @return The database connection
     * @throws Exception If connection cannot be established
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
        plugin.getManagerRegistry().registerManager(daoManager);
        Config config = Config.mainConfigOf(plugin);

        cachedEntities = new ObjectCache<>(
                plugin,
                config.getTimeInMilliseconds("sql.cache-expires").orElse(30 * 60 * 1000L),
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
     * Establishes a database connection for a plugin and creates necessary tables.
     *
     * @param plugin The plugin to connect
     * @param entityClasses Entity classes to create tables for
     */
    @SafeVarargs
    public final void connectPlugin(Plugin plugin, Class<? extends Entity>... entityClasses) {

        if (entityClasses == null) return;

        plugin.getDebug().loading("Connecting database...");

        Config pluginConfig = Config.mainConfigOf(plugin);
        Config xg7PluginsConfig = Config.mainConfigOf(XG7Plugins.getInstance());

        if (!pluginConfig.get("sql", ConfigurationSection.class).isPresent()) {
            plugin.getDebug().severe("Connection aborted!");
            return;
        }

        ConnectionType connectionType = pluginConfig.get("sql.type", ConnectionType.class).orElse(ConnectionType.SQLITE);

        plugin.getDebug().loading("Connection type: " + connectionType);

        Connector connector = connectorRegistry.getConnector(connectionType);

        if (connector == null) {
            plugin.getDebug().severe("Connection type not found!");
            plugin.getDebug().severe("Connection aborted!");
            return;
        }

        try {
            connector.connect(plugin, SQLConfigs.of(pluginConfig, xg7PluginsConfig));
        } catch (Exception e) {
            plugin.getDebug().severe("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        plugin.getDebug().loading("Successfully connected to database!");

        plugin.getDebug().loading("Checking tables...");

        Arrays.stream(entityClasses).forEach(aClass -> tableCreator.createTableOf(plugin, aClass));

        plugin.getDebug().loading("Successfully checked tables!");

    }

    public final void registerDAOs(List<DAO<?, ?>> daos) {
        if (daos == null) return;
        daos.forEach(daoManager::registerDAO);
    }

    /**
     * Disconnects a plugin from its database connection.
     *
     * @param plugin The plugin to disconnect
     */
    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getDebug().loading("Disconnecting database...");
        connectorRegistry.getConnector(plugin).disconnect(plugin);
        plugin.getDebug().loading("Disconnected database!");
    }

    /**
     * Shuts down all database connections and processors.
     * Disconnects all plugins from their database connections.
     *
     * @throws Exception If an error occurs during shutdown
     */
    public void shutdown() throws Exception {
        XG7PluginsAPI.getAllXG7Plugins().forEach(plugin -> {
            try {
                disconnectPlugin(plugin);
            } catch (Exception e) {
                plugin.getDebug().severe("Error while disconnecting database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets a cached entity from the cache using plugin name and ID as key.
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
     * @return CompletableFuture containing true if entity is cached, false otherwise
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
        plugin.getDebug().loading("Reloading database connection...");

        disconnectPlugin(plugin);
        connectPlugin(plugin);

        plugin.getDebug().loading("Reloaded database connection!");
    }

}
