package com.xg7plugins.data.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DBManager {

    private final HashMap<String, Connection> connections = new HashMap<>();
    @Getter
    private final Cache<String, Entity> entitiesCached;


    @SneakyThrows
    public DBManager(XG7Plugins plugin) {

        plugin.getLog().loading("Loading database manager...");

        Config config = plugin.getConfigsManager().getConfig("config");

        entitiesCached = Caffeine.newBuilder().expireAfterAccess(config.getTime("sql.cache-expires").orElse(30 * 60 * 1000L), TimeUnit.MILLISECONDS).build();
    }

    @SneakyThrows
    public void connectPlugin(Plugin plugin, Class<? extends Entity>... entityClasses) {

        if (entityClasses == null) return;

        plugin.getLog().loading("Connecting database...");

        Config pluginConfig = plugin.getConfigsManager().getConfig("config");

        if (pluginConfig == null || !pluginConfig.get("sql", ConfigurationSection.class).isPresent()) {
            plugin.getLog().warn("Connection aborted!");
            return;
        }

        ConnectionType connectionType = pluginConfig.get("sql.type", ConnectionType.class).orElse(ConnectionType.SQLITE);

        String host = pluginConfig.get("sql.host", String.class).orElse(null);
        String port = pluginConfig.get("sql.port", String.class).orElse(null);
        String database = pluginConfig.get("sql.database", String.class).orElse(null);
        String username = pluginConfig.get("sql.username", String.class).orElse(null);
        String password = pluginConfig.get("sql.password", String.class).orElse(null);

        plugin.getLog().loading("Connection type: " + connectionType);

        switch (connectionType) {
            case SQLITE:

                Class.forName("org.sqlite.JDBC");

                File file = new File(plugin.getDataFolder(), "data.db");
                if (!file.exists()) file.createNewFile();

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db"));

                break;
            case MARIADB:

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password));

                break;
            case MYSQL:

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));

                break;
        }

        plugin.getLog().loading("Successfully connected to database!");

        plugin.getLog().loading("Checking tables...");

        Arrays.stream(entityClasses).forEach(aClass -> EntityProcessor.createTableOf(plugin, aClass));

        plugin.getLog().loading("Successfully checked tables!");

    }

    protected void cacheEntity(Object id, Entity entity) {
        entitiesCached.put(id.toString(), entity);
    }

    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getLog().loading("Disconnecting database...");
        if (connections.get(plugin.getName()) != null) connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
        plugin.getLog().loading("Disconnected database!");
    }

    public synchronized CompletableFuture<Query> executeQuery(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = connections.get(plugin.getName());

                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);


                ResultSet rs = ps.executeQuery();

                List<Map<String, Object>> results = new ArrayList<>();

                while (rs.next()) {

                    Map<String, Object> map = new HashMap<>();

                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++)
                        map.put(rs.getMetaData().getTableName(i + 1) + "." + rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));

                    results.add(map);
                }

                return new Query(results.iterator(), this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));

    }

    public synchronized CompletableFuture<ResultSet> executeNormalStatement(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = connections.get(plugin.getName());

                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);

                return ps.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));
    }

    public synchronized CompletableFuture<Void> executeUpdate(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.runAsync(() -> {
            try {
                Connection connection = connections.get(plugin.getName());
                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));
    }





}
