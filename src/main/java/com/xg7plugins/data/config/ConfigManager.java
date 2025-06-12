package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.managers.Manager;
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
public class ConfigManager implements Manager {

    private final HashMap<String, Config> configs = new HashMap<>();
    private final HashMap<Class<?>, ConfigTypeAdapter<?>> adapters = new HashMap<>();

    /**
     * Initializes the ConfigManager with default and custom configurations.
     *
     * @param plugin  The plugin instance that owns these configurations
     * @param configs Array of configuration names to load (can be null)
     */
    public ConfigManager(Plugin plugin, String[] configs) {

        plugin.getLogger().log(Level.CONFIG, "Loading configs of " + plugin.getName());

        putConfig("config", new Config(plugin,"config"));
        putConfig("commands", new Config(plugin,"commands"));

        if (configs == null) return;

        Arrays.stream(configs).forEach(config -> putConfig(config, new Config(plugin, config)));
    }

    /**
     * Reloads all registered configurations from their files.
     */
    public void reloadConfigs() {
        configs.values().forEach(Config::reload);
    }

    /**
     * Registers a configuration with the specified name.
     *
     * @param name   The name to identify the configuration
     * @param config The configuration instance to register
     */
    public void putConfig(String name, Config config) {
        configs.put(name, config);
    }

    /**
     * Retrieves a configuration by its name.
     *
     * @param name The name of the configuration to retrieve
     * @return The configuration instance, or null if not found
     */
    public Config getConfig(String name) {
        return configs.get(name);
    }

    /**
     * Registers a type adapter for converting configuration values.
     *
     * @param tClass  The class type to register the adapter for
     * @param adapter The adapter instance to handle the type conversion
     * @param <T>     The type parameter for the class and adapter
     */
    public <T> void registerAdapter(ConfigTypeAdapter<T> adapter) {
        adapters.put(adapter.getTargetType(), adapter);
    }


}
