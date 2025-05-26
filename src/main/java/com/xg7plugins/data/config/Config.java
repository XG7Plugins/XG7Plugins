package com.xg7plugins.data.config;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.time.TimeParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Configuration manager class that handles YAML configuration files.
 * Provides methods for loading, saving, and accessing configuration data
 * with automatic version control and backup functionality.
 */
@Getter
public class Config {

    private final Plugin plugin;
    private final String name;
    private final File configFile;
    private YamlConfiguration config;

    /**
     * Creates a new Config instance with version control.
     * If the config version differs from the resource version, creates a backup
     * and loads the new version from resources.
     *
     * @param plugin The plugin instance
     * @param name   The name of the configuration file without extension
     */
    @SneakyThrows
    public Config(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        plugin.getLogger().info("Loading " + name + ".yml...");

        File configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(name + ".yml"), StandardCharsets.UTF_8));
        if (!resourceConfig.getString("config-version").equals(config.getString("config-version"))) {

            File backupFile = new File(plugin.getDataFolder(), name + "-old.yml");
            configFile.renameTo(backupFile);

            plugin.saveResource(name + ".yml", true);

            this.configFile = new File(plugin.getDataFolder(), name + ".yml");

            this.config = YamlConfiguration.loadConfiguration(configFile);

            plugin.getLogger().info("Loaded!");

            return;
        }

        this.configFile = configFile;
        this.config = config;
    }

    /**
     * Creates a new Config instance with an existing YamlConfiguration.
     *
     * @param plugin The plugin instance
     * @param config The existing YamlConfiguration to use
     * @param name   The name of the configuration file without extension
     * @throws IOException if unable to create the config file
     */
    public Config(Plugin plugin, YamlConfiguration config, String name) throws IOException {
        this.plugin = plugin;
        this.name = name;
        this.config = config;
        this.configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists()) configFile.createNewFile();
    }  /**
     * Factory method to get or create a Config instance.
     * Returns existing config if available, otherwise creates new one.
     *
     * @param name   The name of the configuration file
     * @param plugin The plugin instance
     * @return The Config instance
     */
    public static Config of(String name, Plugin plugin) {
        if (XG7PluginsAPI.configManager(plugin).getConfigs().containsKey(name)) {
            return XG7PluginsAPI.configManager(plugin).getConfigs().get(name);
        }
        return new Config(plugin, name);
    }

    /**
     * Gets or creates the main configuration file for a plugin.
     *
     * @param plugin The plugin instance
     * @return The main Config instance
     */
    public static Config mainConfigOf(Plugin plugin) {
        return Config.of("config", plugin);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    /**
     * Gets a value from the configuration with type conversion.
     * Supports primitive types, enums, UUIDs, and custom type adapters.
     *
     * @param path              Path to the configuration value
     * @param type              The expected return type class
     * @param ignoreNonexistent Whether to ignore missing values
     * @param optionalTypeArgs  Optional arguments for type conversion
     * @param <T>               The expected return type
     * @return Optional containing the value if present and convertible
     */
    public <T> Optional<T> get(String path, Class<T> type, boolean ignoreNonexistent, Object... optionalTypeArgs) {
        if (!verifyExists(path, ignoreNonexistent)) return Optional.empty();

        if (type == String.class) return Optional.ofNullable(type.cast(config.getString(path)));
        if (type == Integer.class || type == int.class) return Optional.of(type.cast(config.getInt(path)));
        if (type == Boolean.class || type == boolean.class) return Optional.of(type.cast(config.getBoolean(path)));
        if (type == Double.class || type == double.class) return Optional.of(type.cast(config.getDouble(path)));
        if (type == Long.class || type == long.class) return Optional.of(type.cast(config.getLong(path)));
        if (type == Float.class || type == float.class) return Optional.of(type.cast((float) config.getDouble(path)));
        if (type == Short.class || type == short.class) return Optional.of(type.cast((short) config.getInt(path)));
        if (type == ConfigurationSection.class) return Optional.ofNullable(type.cast(config.getConfigurationSection(path)));
        if (type.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;
            Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>) enumClass, config.getString(path).toUpperCase());
            return Optional.of(type.cast(enumValue));
        }
        if (type == UUID.class) return Optional.of((T) UUID.fromString(config.getString(path)));

        if (OfflinePlayer.class.isAssignableFrom(type)) {
            return Optional.of((T) Bukkit.getOfflinePlayer(config.getString(path)));
        }
        if (World.class.isAssignableFrom(type)) {
            return Optional.of((T) Bukkit.getWorld(config.getString(path)));
        }

        ConfigTypeAdapter<T> adapter = (ConfigTypeAdapter<T>) XG7PluginsAPI.configManager(plugin).getAdapters().get(type);

        if (adapter == null) {
            plugin.getDebug().warn("Adapter not found for " + type.getName());
            return Optional.empty();
        }

        return Optional.ofNullable(adapter.fromConfig(get(path, ConfigurationSection.class).orElse(null), optionalTypeArgs));
    }

    /**
     * Verifies if a configuration path exists and has a non-null value.
     * Optionally logs warnings if the path is missing or empty.
     *
     * @param path              Path to verify in the configuration
     * @param ignoreNonexistent Whether to suppress warning messages
     * @return true if path exists and has value, false otherwise
     */
    private boolean verifyExists(String path, boolean ignoreNonexistent) {
        if (!config.contains(path)) {
            if (!ignoreNonexistent) plugin.getDebug().warn(path + " not found in " + name + ".yml");
            return false;
        }
        if (config.get(path) == null) {
            if (!ignoreNonexistent) plugin.getDebug().warn(path + " in " + name + " is empty");
            return false;
        }
        return true;
    }

    public <T> Optional<T> get(String path, Class<T> type, Object... optionalTypeArgs) {
        return get(path,type,false,optionalTypeArgs);
    }


    /**
     * Gets a list of values from the configuration with type conversion.
     * Supports primitive types and maps.
     *
     * @param path Path to the configuration list
     * @param type The expected element type class
     * @param ignoreNonexistent Whether to ignore missing values
     * @param <T> The expected element type
     * @return Optional containing the list if present and convertible
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> getList(String path, Class<T> type, boolean ignoreNonexistent) {
        if (!verifyExists(path, ignoreNonexistent)) return Optional.empty();

        if (type == String.class) return Optional.of((List<T>) config.getStringList(path));
        if (type == Integer.class || type == int.class) return Optional.of((List<T>) config.getIntegerList(path));
        if (type == Boolean.class || type == boolean.class) return Optional.of((List<T>) config.getBooleanList(path));
        if (type == Double.class || type == double.class) return Optional.of((List<T>) config.getDoubleList(path));
        if (type == Long.class || type == long.class) return Optional.of((List<T>) config.getLongList(path));
        if (type == Float.class || type == float.class) return Optional.of((List<T>) config.getFloatList(path));
        if (type == Map.class) return Optional.of((List<T>) config.getMapList(path));
        if (type == Short.class || type == short.class) return Optional.of((List<T>) config.getShortList(path));

        return Optional.empty();
    }
    public <T> Optional<List<T>> getList(String path, Class<T> type) {
        return getList(path,type,false);
    }

    /**
     * Gets a time duration value from the configuration.
     * Converts string time format to milliseconds.
     *
     * @param path              Path to the time value
     * @param ignoreNonexistent Whether to ignore missing values
     * @return Optional containing the time in milliseconds if valid
     */
    public Optional<Long> getTime(String path, boolean ignoreNonexistent) {
        String time = config.getString(path);
        if (time == null) {
            if (!ignoreNonexistent) plugin.getDebug().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }
        long milliseconds;
        try {
            milliseconds = TimeParser.convertToMilliseconds(time);
        } catch (TimeParser.TimeParseException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(milliseconds == 0 ? null : milliseconds);
    }
    
    public Optional<Long> getTime(String path) {
        return getTime(path,false);
    }

    /**
     * Sets a value in the configuration at the specified path.
     *
     * @param path  The path where the value should be set
     * @param value The value to set at the specified path
     */
    public void set(String path, Object value) {
        config.set(path, value);
    }

    /**
     * Checks if a configuration value at the specified path is of the given type.
     * Uses reflection to invoke the appropriate "is" method on the configuration.
     *
     * @param path The path to check
     * @param type The type to check against
     * @param <T>  The type parameter
     * @return true if the value is of the specified type, false otherwise
     */
    @SneakyThrows
    public <T> boolean is(String path, Class<T> type) {
        return (boolean) config.getClass().getMethod("is" + type.getSimpleName(), String.class).invoke(config, path);
    }

    /**
     * Saves the configuration to file.
     * Logs the save operation in debug mode.
     */
    @SneakyThrows
    public void save() {
        plugin.getDebug().info("Saving " + name + ".yml...");
        config.save(configFile);
        plugin.getDebug().info("Saved!");
    }

    /**
     * Reloads the configuration from file.
     * Updates the config manager with the reloaded instance.
     */
    public void reload() {
        plugin.getDebug().info("Reloading " + name + ".yml...");

        this.config = YamlConfiguration.loadConfiguration(configFile);

        XG7PluginsAPI.configManager(plugin).putConfig(name,this);

        plugin.getDebug().info("Reloaded");
    }

}
