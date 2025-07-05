package com.xg7plugins.cache;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import lombok.Getter;

@Getter
@ConfigFile(plugin = XG7Plugins.class, configName = "config", path = "redis-cache.")
public class RedisCacheSection extends ConfigSection {

    private boolean enabled;
    private String host;
    private int port;
    private boolean userAuthEnabled;
    private String username;
    private String password;
    private boolean cacheExpires;
    private int minIdleConnections;
    private int maxIdleConnections;
    private int maxConnections;
    private Time maxWaitTime;


}
