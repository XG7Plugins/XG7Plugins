package com.xg7plugins.data.database;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.TableCreator;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
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

public class DatabaseManager {

    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    @Getter
    private final TableCreator tableCreator = new TableCreator();
    @Getter
    private final ObjectCache<String, Entity> cachedEntities;
    @Getter
    private final DatabaseProcessor processor = new DatabaseProcessor(this);

    public Connection getConnection(Plugin plugin) {
        return connections.get(plugin.getName());
    }

    public DatabaseManager(XG7Plugins plugin) {
        plugin.getLog().loading("Loading database manager...");

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

        plugin.getLog().loading("Connecting database...");

        Config pluginConfig = plugin.getConfigsManager().getConfig("config");

        if (pluginConfig == null || !pluginConfig.get("sql", ConfigurationSection.class).isPresent()) {
            plugin.getLog().severe("Connection aborted!");
            return;
        }

        ConnectionType connectionType = pluginConfig.get("sql.type", ConnectionType.class).orElse(ConnectionType.SQLITE);

        String host = pluginConfig.get("sql.host", String.class).orElse(null);
        String port = pluginConfig.get("sql.port", String.class).orElse(null);
        String database = pluginConfig.get("sql.database", String.class).orElse(null);
        String username = pluginConfig.get("sql.username", String.class).orElse(null);
        String password = pluginConfig.get("sql.password", String.class).orElse(null);

        String additionalArgs = pluginConfig.get("sql.additional-url-args", String.class).orElse("");

        plugin.getLog().loading("Connection type: " + connectionType);

        try {
            switch (connectionType) {
                case SQLITE:

                    Class.forName("org.sqlite.JDBC");

                    File file = new File(plugin.getDataFolder(), "data.db");
                    if (!file.exists()) file.createNewFile();

                    connections.put(plugin.getName(), DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db"));

                    break;
                case MARIADB:

                    connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database + "?" + additionalArgs, username, password));

                    break;
                case MYSQL:

                    connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?" + additionalArgs, username, password));

                    break;
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            plugin.getLog().severe("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
            return;
        }



        plugin.getLog().loading("Successfully connected to database!");

        try {
            connections.get(plugin.getName()).setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        plugin.getLog().loading("Checking tables...");

        Arrays.stream(entityClasses).forEach(aClass -> tableCreator.createTableOf(plugin, aClass).join());

        plugin.getLog().loading("Successfully checked tables!");

    }

    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getLog().loading("Disconnecting database...");
        if (connections.get(plugin.getName()) != null) connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
        plugin.getLog().loading("Disconnected database!");
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
