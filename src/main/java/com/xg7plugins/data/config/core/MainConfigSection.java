package com.xg7plugins.data.config.core;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import lombok.Getter;

/**
 * This class represents the majority of the main config keys,
 * making easier to access
 */
@Getter
@ConfigFile(plugin = XG7Plugins.class, configName = "config")
public class MainConfigSection extends ConfigSection {

    private String prefix;
    private String pluginServerName;
    private boolean debugEnabled;
    private boolean langEnabled;
    private String mainLang;
    private boolean antiTab;
    private Time langCacheExpires;
    private boolean autoChoseLang;
    private Time cooldownToToggleLang;
    private Time jsonCacheExpires;
    private Time httpRequestTimeout;
    private boolean geyserFormsEnabled;
    private boolean langFormEnabled;
    private int scheduledTasksThreads;
    private Time playerCooldownTaskDelay;
    private Time menuCacheExpires;
    private boolean helpCommandInGui;
    private boolean helpCommandForm;

}
