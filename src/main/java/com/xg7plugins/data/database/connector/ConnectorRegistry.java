package com.xg7plugins.data.database.connector;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.ConnectionType;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectorRegistry {

    private final ConcurrentHashMap<ConnectionType, Connector> connectors = new ConcurrentHashMap<>();

    public void registerConnector(Connector connector) {
        connectors.put(connector.getType(), connector);
    }
    public Connector getConnector(ConnectionType type) {
        return connectors.get(type);
    }
    public Connector getConnector(Plugin plugin) {
        Config config = Config.mainConfigOf(plugin);
        ConnectionType type = config.get("sql.type", ConnectionType.class).orElse(null);
        if (type != null) return connectors.get(type);
        return null;
    }
    public Connection getConnection(Plugin plugin) throws Exception {
        Config config = Config.mainConfigOf(plugin);
        Connector connector = connectors.get(config.get("sql.type", ConnectionType.class).orElse(null));
        if (connector != null) return connector.getConnection(null);
        return null;
    }
}
