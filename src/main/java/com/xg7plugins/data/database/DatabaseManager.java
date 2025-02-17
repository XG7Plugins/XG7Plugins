package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class DatabaseManager {

    private final ConcurrentHashMap<String, HikariDataSource> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Connection> sqliteConnections = new ConcurrentHashMap<>();
    private final TableCreator tableCreator = new TableCreator();
    private final ObjectCache<String, Entity> cachedEntities;
    private final DatabaseProcessor processor = new DatabaseProcessor(this);

    public Connection getConnection(Plugin plugin) throws SQLException {
        if (sqliteConnections.containsKey(plugin.getName())) return sqliteConnections.get(plugin.getName());
        return connections.get(plugin.getName()).getConnection();
    }

    public DatabaseManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");

        cachedEntities = new ObjectCache<>(
                plugin,
                config.getTime("sql.cache-expires").orElse(30 * 60 * 1000L),
                false,
                "cached-entities",
                false,
                String.class,
                Entity.class
        );

    }

    @SafeVarargs
    public final void connectPlugin(Plugin plugin, Class<? extends Entity>... entityClasses) {

        if (entityClasses == null) return;

        plugin.getDebug().loading("Connecting database...");

        Config pluginConfig = Config.mainConfigOf(plugin);
        Config xg7PluginsConfig = Config.mainConfigOf(XG7Plugins.getInstance());

        if (!pluginConfig.get("sql", ConfigurationSection.class).isPresent()) {
            plugin.getDebug().error("Connection aborted!");
            return;
        }

        ConnectionType connectionType = pluginConfig.get("sql.type", ConnectionType.class).orElse(ConnectionType.SQLITE);

        String host = pluginConfig.get("sql.host", String.class).orElse(null);
        String port = pluginConfig.get("sql.port", String.class).orElse(null);
        String database = pluginConfig.get("sql.database", String.class).orElse(null);
        String username = pluginConfig.get("sql.username", String.class).orElse(null);
        String password = pluginConfig.get("sql.password", String.class).orElse(null);

        String additionalArgs = pluginConfig.get("sql.additional-url-args", String.class).orElse("");

        plugin.getDebug().loading("Connection type: " + connectionType);

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setAutoCommit(false);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(xg7PluginsConfig.get("sql.max-pool-size", Integer.class).orElse(10));
        hikariConfig.setPoolName(plugin.getName() + "-pool");
        hikariConfig.setMinimumIdle(xg7PluginsConfig.get("sql.min-idle-connections", Integer.class).orElse(5));
        hikariConfig.setConnectionTimeout(xg7PluginsConfig.getTime("sql.connection-timeout").orElse(5000L));
        hikariConfig.setIdleTimeout(xg7PluginsConfig.getTime("sql.idle-timeout").orElse(600000L));
        hikariConfig.setConnectionTestQuery("SELECT 1");

        try {
            switch (connectionType) {
                case SQLITE:

                    Class.forName("org.sqlite.JDBC");

                    File file = new File(plugin.getDataFolder(), "data.db");
                    if (!file.exists()) file.createNewFile();

                    sqliteConnections.put(plugin.getName(), DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db", username, password));

                    break;
                case MARIADB:

                    Class.forName("org.mariadb.jdbc.Driver");

                    hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?" + additionalArgs);

                    connections.put(plugin.getName(), new HikariDataSource(hikariConfig));

                    break;
                case MYSQL:

                    hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?" + additionalArgs);

                    connections.put(plugin.getName(), new HikariDataSource(hikariConfig));

                    break;
            }
        } catch (ClassNotFoundException | IOException e) {
            plugin.getDebug().error("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        plugin.getDebug().loading("Successfully connected to database!");

        plugin.getDebug().loading("Checking tables...");

        Arrays.stream(entityClasses).forEach(aClass -> tableCreator.createTableOf(plugin, aClass).join());

        plugin.getDebug().loading("Successfully checked tables!");



    }

    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getDebug().loading("Disconnecting database...");
        if (connections.get(plugin.getName()) != null) connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
        plugin.getDebug().loading("Disconnected database!");
    }

    public <T extends Entity> CompletableFuture<T> getCachedEntity(Plugin plugin, String id) {
        return (CompletableFuture<T>) cachedEntities.get(plugin.getName() + ":" + id);
    }
    public CompletableFuture<Boolean> containsCachedEntity(Plugin plugin, String id) {
        return cachedEntities.containsKey(plugin.getName() + ":" + id);
    }
    public void cacheEntity(Plugin plugin, String id, Entity entity) {
        cachedEntities.put(plugin.getName() + ":" + id, entity);
    }

}
