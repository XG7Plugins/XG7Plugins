package com.xg7plugins.data.database.connector;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.Data;

@Data
public class SQLConfigs {

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

    private short queryThreads;
    private long processInterval;

    public SQLConfigs(Config pluginConfig) {
        host = pluginConfig.get("sql.host", String.class).orElse(null);
        port = pluginConfig.get("sql.port", Integer.class).orElse(0);
        database = pluginConfig.get("sql.database", String.class).orElse(null);
        username = pluginConfig.get("sql.username", String.class).orElse(null);
        password = pluginConfig.get("sql.password", String.class).orElse(null);

        connectionString = pluginConfig.get("sql.url", String.class).orElse(null);
    }

    public SQLConfigs(Config pluginConfig, Config mainConfig) {
        this(pluginConfig);
        this.cacheExpires = mainConfig.getTime("sql.cache-expires").orElse(0L);

        this.connectionTimeout = mainConfig.getTime("sql.connection-timeout").orElse(0L);

        this.idleTimeout = mainConfig.getTime("sql.idle-timeout").orElse(0L);

        this.maxPoolSize = mainConfig.get("sql.max-pool-size", Integer.class)
                .orElse(10); // Valor padrão sugerido

        this.minIdle = mainConfig.get("sql.min-idle-connections", Integer.class)
                .orElse(5); // Valor padrão sugerido

        this.keepAliveTime = mainConfig.getTime("sql.keep-alive-time").orElse(0L);

        this.queryThreads = mainConfig.get("sql.query-processor-threads", Short.class)
                .orElse((short) 4);

        this.processInterval = mainConfig.getTime("sql.sql-command-processing-interval").orElse(0L);
    }

    public static SQLConfigs of(Config pluginConfig) {
        return new SQLConfigs(pluginConfig);
    }
    public static SQLConfigs of(Config pluginConfig, Config mainConfig) {
        return new SQLConfigs(pluginConfig,mainConfig);
    }

    public boolean checkCredentials() {
        return !(host == null || port == 0 || database == null ||
                host.isEmpty() || database.isEmpty());
    }
    public boolean hasURL() {
        return connectionString != null && !connectionString.isEmpty();
    }



}
