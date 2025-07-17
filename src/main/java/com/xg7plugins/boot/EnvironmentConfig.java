package com.xg7plugins.boot;

import lombok.Data;

import java.util.List;


/**
 * Configuration class for managing environment-specific settings.
 * Contains settings related to plugin features.
 */

@Data
public class EnvironmentConfig {
    /**
     * The custom prefix configured for plugin messages
     */
    private String customPrefix;

    /**
     * The default plugin prefix
     */
    private String prefix;

    /**
     * List of worlds where the plugin's functionality is enabled, if supported by the plugin.
     */
    private List<String> enabledWorlds;
}
