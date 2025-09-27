package com.xg7plugins.data.database.connector;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.data.database.ConnectionType;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing database connectors.
 * This class stores and retrieves database connectors for different connection types.
 * Ensures thread-safe access to connectors using ConcurrentHashMap.
 */
public class ConnectorRegistry {

    private final ConcurrentHashMap<ConnectionType, Connector> connectors = new ConcurrentHashMap<>();

    /**
     * Registers a database connector with its connection type.
     *
     * @param connector The connector to register
     */
    public void registerConnector(Connector connector) {
        connectors.put(connector.getType(), connector);
    }

    /**
     * Retrieves a connector by its connection type.
     *
     * @param type The connection type
     * @return The corresponding connector, or null if not found
     */
    public Connector getConnector(ConnectionType type) {
        return connectors.get(type);
    }

    /**
     * Gets the appropriate connector for a plugin based on its configuration.
     *
     * @param plugin The plugin to get the connector for
     * @return The connector for the plugin, or null if not configured
     */
    public Connector getConnector(Plugin plugin) {
        return connectors.get(ConfigFile.mainConfigOf(plugin).section("sql").get("type", ConnectionType.SQLITE));
    }

    /**
     * Establishes a database connection for a plugin using its configuration.
     *
     * @param plugin The plugin to get the connection for
     * @return A database connection for the plugin, or null if not configured
     * @throws Exception If connection creation fails
     */
    public Connection getConnection(Plugin plugin) throws Exception {
        return getConnector(plugin).getConnection(plugin);
    }
}