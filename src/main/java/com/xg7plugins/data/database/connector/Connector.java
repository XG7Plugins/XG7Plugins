package com.xg7plugins.data.database.connector;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;

import java.sql.Connection;

public interface Connector {

    void connect(Plugin plugin, SQLConfigs sqlConfigs) throws Exception;
    void disconnect(Plugin plugin) throws Exception;

    Connection getConnection(Plugin plugin) throws Exception;

    ConnectionType getType();


}
