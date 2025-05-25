package com.xg7plugins.data.database.connector;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.ConnectionType;

import java.sql.Connection;

/**
 * Interface for managing database connections in the plugin system.
 * Provides methods for establishing, managing, and closing database connections
 * for different types of databases.
 */
public interface Connector {

    /**
     * Establishes a database connection for the specified plugin using the provided SQL configuration.
     *
     * @param plugin     The plugin requesting the connection
     * @param sqlConfigs Configuration settings for the SQL connection
     * @throws Exception if the connection cannot be established
     */
    void connect(Plugin plugin, SQLConfigs sqlConfigs) throws Exception;

    /**
     * Disconnects and closes the database connection for the specified plugin.
     *
     * @param plugin The plugin whose connection should be closed
     * @throws Exception if there is an error during disconnection
     */
    void disconnect(Plugin plugin) throws Exception;

    /**
     * Retrieves the active database connection for the specified plugin.
     *
     * @param plugin The plugin requesting its connection
     * @return The Connection object associated with the plugin
     * @throws Exception if the connection cannot be retrieved
     */
    Connection getConnection(Plugin plugin) throws Exception;

    /**
     * Gets the type of database connection being used.
     *
     * @return The ConnectionType enum value representing the database type
     */
    ConnectionType getType();


}
