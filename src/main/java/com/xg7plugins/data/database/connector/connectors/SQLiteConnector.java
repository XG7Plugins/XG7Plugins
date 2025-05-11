package com.xg7plugins.data.database.connector.connectors;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.SQLConfigs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteConnector implements Connector {

    protected final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(Plugin plugin, SQLConfigs sqlConfigs) throws Exception {

        if (!ConnectionType.SQLITE.isDriverLoaded()) return;

        File file = new File(plugin.getDataFolder(), "data.db");
        if (!file.exists()) file.createNewFile();

        Connection sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db", sqlConfigs.getUsername(), sqlConfigs.getPassword());

        sqliteConnection.setAutoCommit(false);

        connections.put(plugin.getName(), sqliteConnection);
    }

    @Override
    public void disconnect(Plugin plugin) throws Exception {
        if (!connections.containsKey(plugin.getName())) return;
        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
    }

    @Override
    public Connection getConnection(Plugin plugin) {
        return connections.get(plugin.getName());
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.SQLITE;
    }
}
