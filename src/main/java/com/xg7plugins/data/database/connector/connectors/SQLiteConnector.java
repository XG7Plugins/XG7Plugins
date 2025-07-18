package com.xg7plugins.data.database.connector.connectors;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.SQLConfigs;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SQLiteConnector implements Connector {

    protected final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(Plugin plugin, SQLConfigs sqlConfigs) throws Exception {

        if (!ConnectionType.SQLITE.isDriverLoaded()) return;

        plugin.getDebug().info("Connecting " + plugin.getName() + " to SQLite database...");

        File file = new File(plugin.getDataFolder(), "data.db");
        if (!file.exists()) file.createNewFile();

        Connection sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db", sqlConfigs.getUsername(), sqlConfigs.getPassword());

        sqliteConnection.setAutoCommit(false);

        connections.put(plugin.getName(), sqliteConnection);

        XG7PluginsAPI.taskManager().runTimerTask(XG7PluginsAPI.taskManager().getTimerTask(XG7Plugins.getInstance(), "keep-alive-database"));

        plugin.getDebug().info("Success!");

    }

    @Override
    public void disconnect(Plugin plugin) throws Exception {
        if (!connections.containsKey(plugin.getName())) return;

        plugin.getDebug().info("Disconnecting " + plugin.getName() + " from SQLite database...");

        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());

        plugin.getDebug().info("Success!");
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
