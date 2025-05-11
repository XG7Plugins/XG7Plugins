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

        HikariConfig hikariConfig = setupHikariConfig(plugin, sqlConfigs);

        hikariConfig.setJdbcUrl(sqlConfigs.hasURL() ? sqlConfigs.getConnectionString() : "jdbc:mysql://" + sqlConfigs.getHost() + ":" + sqlConfigs.getPort() + "/" + sqlConfigs.getDatabase());

        connections.put(plugin.getName(), new HikariDataSource(hikariConfig));

    }


    @Override
    public void disconnect(Plugin plugin) {
        if (!connections.containsKey(plugin.getName())) return;
        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
    }

    @Override
    public Connection getConnection(Plugin plugin) throws Exception {
        return connections.get(plugin.getName()).getConnection();
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.MYSQL;
    }

    protected HikariConfig setupHikariConfig(Plugin plugin, SQLConfigs sqlConfigs) {
        HikariConfig hikariConfig = new HikariConfig();

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
