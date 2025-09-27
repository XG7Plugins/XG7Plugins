package com.xg7plugins.tasks.plugin_tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.connector.connectors.SQLiteConnector;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;

public class DatabaseKeepAlive extends TimerTask {

    public DatabaseKeepAlive() {
        super(
                XG7Plugins.getInstance(),
                "keep-alive-database",
                0,
                ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("sql").getTimeInMilliseconds("keep-alive-delay", 10 * 60 * 1000L),
                TaskState.RUNNING,
                null
        );
    }


    @Override
    public void run() {
        DatabaseManager databaseManager = XG7PluginsAPI.database();

        SQLiteConnector sqLiteConnector = (SQLiteConnector) databaseManager.getConnectorRegistry().getConnector(ConnectionType.SQLITE);

        sqLiteConnector.getConnections().forEach((pluginName, connection) -> {
            try {
                connection.createStatement().execute("SELECT 1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
