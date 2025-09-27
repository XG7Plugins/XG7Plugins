package com.xg7plugins.data.database.connector;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.data.database.ConnectionType;
import lombok.Data;

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
    public SQLConfigs(ConfigFile pluginConfig) {

        ConfigSection config = pluginConfig.root();

        host = config.get("sql.host");
        port = config.get("sql.port", 0);
        database = config.get("sql.database");
        username = config.get("sql.username");
        password = config.get("sql.password");

        connectionString = config.get("sql.url");
        connectionType = config.get("sql.type", ConnectionType.SQLITE);
    }

    /**
     * Creates a complete configuration using both plugin and main configs.
     *
     * @param pluginConfig The plugin-specific configuration
     * @param mainConfig The main system configuration with defaults
     */
    public SQLConfigs(ConfigFile pluginConfig, ConfigFile mainConfig) {
        this(pluginConfig);

        ConfigSection config = mainConfig.root();

        this.cacheExpires = config.getTimeInMilliseconds("sql.cache-expires", 0L);

        this.connectionTimeout = config.getTimeInMilliseconds("sql.connection-timeout", 0L);

        this.idleTimeout = config.getTimeInMilliseconds("sql.idle-timeout", 0L);

        this.maxPoolSize = config.get("sql.max-pool-size", 10);

        this.minIdle = config.get("sql.min-idle-connections", 5);

        this.keepAliveTime = config.getTimeInMilliseconds("sql.keep-alive-delay", 0L);
    }

    /**
     * Factory method to create a config from a plugin configuration.
     *
     * @param pluginConfig The plugin configuration
     * @return A new SQLConfigs instance
     */
    public static SQLConfigs of(ConfigFile pluginConfig) {
        return new SQLConfigs(pluginConfig);
    }
    
    /**
     * Factory method to create a config from plugin and main configurations.
     *
     * @param pluginConfig The plugin configuration
     * @param mainConfig The main system configuration
     * @return A new SQLConfigs instance with complete settings
     */
    public static SQLConfigs of(ConfigFile pluginConfig, ConfigFile mainConfig) {
        return new SQLConfigs(pluginConfig,mainConfig);
    }

    public static SQLConfigs of(Plugin plugin) {
        return of(ConfigFile.mainConfigOf(plugin), ConfigFile.mainConfigOf(XG7Plugins.getInstance()));
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