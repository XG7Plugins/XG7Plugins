package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
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
import java.util.concurrent.CompletableFuture;

@Getter
public class DatabaseManager implements Manager {

    private final DatabaseProcessor processor = new DatabaseProcessor(this);
    private final TableCreator tableCreator = new TableCreator();
    private final ConnectorRegistry connectorRegistry;

    private final ObjectCache<String, Entity> cachedEntities;

    public Connection getConnection(Plugin plugin) throws Exception {
        return connectorRegistry.getConnection(plugin);
    }

    public DatabaseManager(XG7Plugins plugin) {
        Config config = Config.mainConfigOf(plugin);

        cachedEntities = new ObjectCache<>(
                plugin,
                config.getTime("sql.cache-expires").orElse(30 * 60 * 1000L),
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

        Arrays.stream(entityClasses).forEach(aClass -> tableCreator.createTableOf(plugin, aClass).join());

        plugin.getDebug().loading("Successfully checked tables!");

    }

    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getDebug().loading("Disconnecting database...");
        connectorRegistry.getConnector(plugin).disconnect(plugin);
        plugin.getDebug().loading("Disconnected database!");
    }

    public void shutdown() throws Exception {
        processor.shutdown();
        XG7PluginsAPI.getAllXG7Plugins().forEach(plugin -> {
            try {
                disconnectPlugin(plugin);
            } catch (Exception e) {
                plugin.getDebug().severe("Error while disconnecting database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public <T extends Entity> CompletableFuture<T> getCachedEntity(@NotNull Plugin plugin, String id) {
        return (CompletableFuture<T>) cachedEntities.get(plugin.getName() + ":" + id);
    }
    public CompletableFuture<Boolean> containsCachedEntity(@NotNull Plugin plugin, String id) {
        return cachedEntities.containsKey(plugin.getName() + ":" + id);
    }
    public void cacheEntity(@NotNull Plugin plugin, String id, Entity entity) {
        cachedEntities.put(plugin.getName() + ":" + id, entity);
    }
    public void unCacheEntity(@NotNull Plugin plugin, String id) {
        cachedEntities.remove(plugin.getName() + ":" + id);
    }

    public void reloadConnection(Plugin plugin) {
        plugin.getDebug().loading("Reloading database connection...");

        disconnectPlugin(plugin);
        connectPlugin(plugin);

        plugin.getDebug().loading("Reloaded database connection!");
    }

}
