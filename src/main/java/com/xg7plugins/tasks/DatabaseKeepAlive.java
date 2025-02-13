package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.DatabaseManager;

public class DatabaseKeepAlive extends Task {

    public DatabaseKeepAlive() {
        super(XG7Plugins.getInstance(), "keep-alive-database", true, true, Config.mainConfigOf(XG7Plugins.getInstance()).getTime("sql.keep-alive-time").orElse(10 * 60 * 1000L), TaskState.RUNNING, null);
    }

    @Override
    public void run() {

        DatabaseManager databaseManager = XG7Plugins.database();

        databaseManager.getConnections().forEach((pluginName, dataSource) -> {
            try {
                dataSource.getConnection().createStatement().execute("SELECT 1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Keep the database connection alive
    }
}
