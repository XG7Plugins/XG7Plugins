package com.xg7plugins.data.database.connector.connectors;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;

import com.xg7plugins.data.database.connector.SQLConfigs;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class MariaDBConnector extends MySQLConnector {
    @Override
    public void connect(Plugin plugin, SQLConfigs sqlConfigs) {
        if (!ConnectionType.MARIADB.isDriverLoaded()) return;

        plugin.getDebug().info("Connecting " + plugin.getName() + " to MariaDB database...");

        HikariConfig hikariConfig = setupHikariConfig(plugin, "jdbc:mariadb://", ConnectionType.MARIADB, sqlConfigs);

        connections.put(plugin.getName(), new HikariDataSource(hikariConfig));

        plugin.getDebug().info("Success!");

    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.MARIADB;
    }
}
