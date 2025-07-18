package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.managers.Manager;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    private final HashMap<Class<? extends ConfigSection>, ConfigSection> sections = new HashMap<>();
    private final HashMap<String, Config> configs = new HashMap<>();
    private final HashMap<Class<?>, ConfigTypeAdapter<?>> adapters = new HashMap<>();

    private final Plugin plugin;

    /**
     * Initializes the ConfigManager with default and custom configurations.
     *
     * @param plugin  The plugin instance that owns these configurations
     * @param configs Array of configuration names to load (can be null)
     */
    public ConfigManager(Plugin plugin, String[] configs) {

        plugin.getLogger().log(Level.CONFIG, "Loading configs of " + plugin.getName());

        this.plugin = plugin;

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
        for (ConfigSection configSection : sections.values()) {
            try {
                configSection.setFieldValues();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
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
     * @param adapter The adapter instance to handle the type conversion
     * @param <T>     The type parameter for the adapter
     */
    public <T> void registerAdapter(ConfigTypeAdapter<T> adapter) {
        plugin.getDebug().info("Registering type adapter for: " + adapter.getTargetType().getSimpleName());
        adapters.put(adapter.getTargetType(), adapter);
    }

    /**
     * Registers all configuration sections defined in the plugin setup.
     * Creates instances of config sections using reflection and registers them.
     *
     * @throws NoSuchMethodException     If the constructor is not found
     * @throws InvocationTargetException If the constructor invocation fails
     * @throws InstantiationException    If instantiation fails
     * @throws IllegalAccessException    If access is denied
     */
    public void registerSections() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends ConfigSection>[] configSections = plugin.getPluginSetup().configSections();
        for (Class<? extends ConfigSection> configSection : configSections) {
            Constructor<? extends ConfigSection> constructor = configSection.getDeclaredConstructor();
            constructor.setAccessible(true);
            ConfigSection instance = constructor.newInstance();
            registerConfigSection(instance);
        }
    }

    /**
     * Registers a configuration section instance.
     * Stores the section instance mapped to its class type.
     *
     * @param section The configuration section to register
     */
    public void registerConfigSection(ConfigSection section) {
        plugin.getDebug().info("Registering config section: " + section.getClass().getSimpleName());
        sections.put(section.getClass(), section);
    }

    /**
     * Gets a configuration section by its class type.
     * Returns the registered instance of the specified config section class.
     *
     * @param clazz The class of the config section to retrieve
     * @return The config section instance
     */
    public <T extends ConfigSection> T getConfigSection(Class<T> clazz) {
        return (T) sections.get(clazz);
    }

}
