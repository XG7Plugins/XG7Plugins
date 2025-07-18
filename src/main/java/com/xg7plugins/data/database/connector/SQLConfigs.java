package com.xg7plugins.data.database.connector;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.ConnectionType;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

/**
 * Configuration holder for SQL database connections.
 * Stores basic connection parameters and pool settings extracted from plugin configurations.
 */
@Data
public class SQLConfigs {

    private final ConnectionType connectionType;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String connectionString;
    private long cacheExpires;
    private long connectionTimeout;
    private long idleTimeout;
    private int maxPoolSize;
    private int minIdle;
    private long keepAliveTime;

    /**
     * Creates a basic configuration from plugin config.
     *
     * @param pluginConfig The plugin configuration
     */
    public SQLConfigs(Config pluginConfig) {
        host = pluginConfig.get("sql.host", String.class).orElse(null);
        port = pluginConfig.get("sql.port", Integer.class).orElse(0);
        database = pluginConfig.get("sql.database", String.class).orElse(null);
        username = pluginConfig.get("sql.username", String.class).orElse(null);
        password = pluginConfig.get("sql.password", String.class).orElse(null);

        connectionString = pluginConfig.get("sql.url", String.class, true).orElse(null);
        connectionType = pluginConfig.get("sql.type", ConnectionType.class).orElse(ConnectionType.SQLITE);
    }

    /**
     * Creates a complete configuration using both plugin and main configs.
     *
     * @param pluginConfig The plugin-specific configuration
     * @param mainConfig The main system configuration with defaults
     */
    public SQLConfigs(Config pluginConfig, Config mainConfig) {
        this(pluginConfig);
        this.cacheExpires = mainConfig.getTimeInMilliseconds("sql.cache-expires").orElse(0L);

        this.connectionTimeout = mainConfig.getTimeInMilliseconds("sql.connection-timeout").orElse(0L);

        this.idleTimeout = mainConfig.getTimeInMilliseconds("sql.idle-timeout").orElse(0L);

        this.maxPoolSize = mainConfig.get("sql.max-pool-size", Integer.class)
                .orElse(10); // Default suggested value

        this.minIdle = mainConfig.get("sql.min-idle-connections", Integer.class)
                .orElse(5); // Default suggested value

        this.keepAliveTime = mainConfig.getTimeInMilliseconds("sql.keep-alive-delay").orElse(0L);
    }

    /**
     * Factory method to create a config from a plugin configuration.
     *
     * @param pluginConfig The plugin configuration
     * @return A new SQLConfigs instance
     */
    public static SQLConfigs of(Config pluginConfig) {
        return new SQLConfigs(pluginConfig);
    }
    
    /**
     * Factory method to create a config from plugin and main configurations.
     *
     * @param pluginConfig The plugin configuration
     * @param mainConfig The main system configuration
     * @return A new SQLConfigs instance with complete settings
     */
    public static SQLConfigs of(Config pluginConfig, Config mainConfig) {
        return new SQLConfigs(pluginConfig,mainConfig);
    }

    public static SQLConfigs of(Plugin plugin) {
        return of(Config.mainConfigOf(plugin), Config.mainConfigOf(XG7Plugins.getInstance()));
    }

    /**
     * Checks if the necessary database credentials are configured.
     *
     * @return true if credentials are properly configured, false otherwise
     */
    public boolean checkCredentials() {
        return !(host == null || port == 0 || database == null || host.isEmpty() || database.isEmpty());
    }
    
    /**
     * Checks if a custom connection URL is configured.
     *
     * @return true if a connection string is available, false otherwise
     */
    public boolean hasURL() {
        return connectionString != null && !connectionString.isEmpty();
    }
}