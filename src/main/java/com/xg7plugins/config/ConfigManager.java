package com.xg7plugins.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Manages configuration files for plugins.
 * Handles loading, reloading, and accessing configuration files.
 * Also provides type adaptation support for configuration values.
 */
@Getter
public class ConfigManager {

    private final HashMap<String, ConfigFile> configs = new HashMap<>();
    private final HashMap<Class<?>, ConfigTypeAdapter<?>> adapters = new HashMap<>();

    private final Plugin plugin;

    /**
     * Initializes the ConfigManager with default and custom configurations.
     *
     * @param plugin  The plugin instance that owns these configurations
     * @param configs Array of configuration names to load (can be null)
     */
    public ConfigManager(Plugin plugin, String[] configs) {

        plugin.getJavaPlugin().getLogger().log(Level.CONFIG, "Loading configs of " + plugin.getName());

        this.plugin = plugin;

        putConfig(new ConfigFile(plugin,"config"));
        putConfig(new ConfigFile(plugin,"commands"));

        if (configs == null) return;

        Arrays.stream(configs).forEach(config -> putConfig(new ConfigFile(plugin, config)));

    }

    /**
     * Reloads all registered configurations from their files.
     */
    public void reloadConfigs() {
        configs.values().forEach(ConfigFile::reload);
    }

    /**
     * Registers a configuration with the specified name.
     *
     * @param config The configuration instance to register
     */
    public void putConfig(ConfigFile config) {
        configs.put(config.getName(), config);
    }

    /**
     * Retrieves a configuration by its name.
     *
     * @param name The name of the configuration to retrieve
     * @return The configuration instance, or null if not found
     */
    public ConfigFile getConfig(String name) {
        return configs.get(name);
    }

    /**
     * Registers a type adapter for converting configuration values.
     *
     * @param adapter The adapter instance to handle the type conversion
     * @param <T>     The type parameter for the adapter
     */
    public <T> void registerAdapter(ConfigTypeAdapter<T> adapter) {
        plugin.getDebug().info("load", "Registering type adapter for: " + adapter.getTargetType().getSimpleName());
        adapters.put(adapter.getTargetType(), adapter);
    }

}
