package com.xg7plugins.data.database.connector.connectors;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.connector.SQLConfigs;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SQLiteConnector implements Connector {

    protected final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(Plugin plugin, SQLConfigs sqlConfigs) throws Exception {

        if (!ConnectionType.SQLITE.isDriverLoaded()) return;

        plugin.getDebug().info("database", "Connecting " + plugin.getName() + " to SQLite database...");

        File file = new File(plugin.getJavaPlugin().getDataFolder(), "data.db");
        if (!file.exists()) file.createNewFile();

        Connection sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getJavaPlugin().getDataFolder().getPath() + "/data.db", sqlConfigs.getUsername(), sqlConfigs.getPassword());

        sqliteConnection.setAutoCommit(false);

        connections.put(plugin.getName(), sqliteConnection);

        XG7Plugins.getAPI().taskManager().runTimerTask(XG7Plugins.getAPI().getTimerTask(XG7Plugins.getPluginID("keep-alive-database")));

        plugin.getDebug().info("database", "Success!");

    }

    @Override
    public void disconnect(Plugin plugin) throws Exception {
        if (!connections.containsKey(plugin.getName())) return;

        plugin.getDebug().info("database", "Disconnecting " + plugin.getName() + " from SQLite database...");

        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());

        plugin.getDebug().info("database", "Success!");
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
