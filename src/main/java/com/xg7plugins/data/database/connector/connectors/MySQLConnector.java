package com.xg7plugins.data.database.connector.connectors;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.SQLConfigs;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

public class MySQLConnector implements Connector {

    protected final ConcurrentHashMap<String, HikariDataSource> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(Plugin plugin, SQLConfigs sqlConfigs) {
        if (!ConnectionType.MYSQL.isDriverLoaded()) return;

        plugin.getDebug().info("database", "Connecting " + plugin.getName() + " to MySQL database...");

        HikariConfig hikariConfig = setupHikariConfig(plugin, "jdbc:mysql://", ConnectionType.MYSQL, sqlConfigs);

        connections.put(plugin.getName(), new HikariDataSource(hikariConfig));

        plugin.getDebug().info("database", "Success!");
    }


    @Override
    public void disconnect(Plugin plugin) {
        if (!connections.containsKey(plugin.getName())) return;

        plugin.getDebug().info("database", "Disconnecting " + plugin.getName() + " from SQL database...");

        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());

        plugin.getDebug().info("database", "Success!");
    }

    @Override
    public Connection getConnection(Plugin plugin) throws Exception {
        return connections.get(plugin.getName()).getConnection();
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.MYSQL;
    }

    protected HikariConfig setupHikariConfig(Plugin plugin, String sqlUrl, ConnectionType type, SQLConfigs sqlConfigs) {

        plugin.getDebug().info("database", "Setting up HikariCP configuration for " + plugin.getName() + "...");

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(type.getDriverClassName());

        hikariConfig.setJdbcUrl(sqlConfigs.hasURL() ? sqlConfigs.getConnectionString() : sqlUrl + sqlConfigs.getHost() + ":" + sqlConfigs.getPort() + "/" + sqlConfigs.getDatabase());

        if (sqlConfigs.checkCredentials()) {
            hikariConfig.setPassword(sqlConfigs.getPassword());
            hikariConfig.setUsername(sqlConfigs.getUsername());
        }

        hikariConfig.setAutoCommit(false);
        hikariConfig.setMaximumPoolSize(sqlConfigs.getMaxPoolSize());
        hikariConfig.setPoolName(plugin.getName() + "-pool");
        hikariConfig.setMinimumIdle(sqlConfigs.getMinIdle());
        hikariConfig.setConnectionTimeout(sqlConfigs.getConnectionTimeout());
        hikariConfig.setIdleTimeout(sqlConfigs.getIdleTimeout());
        hikariConfig.setConnectionTestQuery("SELECT 1");

        return hikariConfig;
    }

}
