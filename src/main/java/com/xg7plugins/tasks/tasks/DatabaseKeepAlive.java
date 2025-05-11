package com.xg7plugins.tasks.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.connector.connectors.SQLiteConnector;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;

public class DatabaseKeepAlive extends Task {

    public DatabaseKeepAlive() {
        super(XG7Plugins.getInstance(), "keep-alive-database", true, true, Config.mainConfigOf(XG7Plugins.getInstance()).getTime("sql.keep-alive-time").orElse(10 * 60 * 1000L), TaskState.RUNNING, null);
    }

    @Override
    public void run() {

        DatabaseManager databaseManager = XG7PluginsAPI.database();

        ((SQLiteConnector) databaseManager.getConnectorRegistry().getConnector(ConnectionType.SQLITE))
                .getConnections().forEach((pluginName, connection) -> {
            try {
                connection.createStatement().execute("SELECT 1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Keep the database connection alive
    }
}
