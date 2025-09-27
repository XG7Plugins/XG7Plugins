package com.xg7plugins.config.file;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.reflect.TypeToken;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.utils.time.TimeParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Configuration manager class that handles YAML configuration files.
 * Provides methods for loading, saving, and accessing configuration data
 * with automatic version control and backup functionality.
 */
@Getter
public class ConfigSection {

    private final ConfigFile file;
    private final String parentPath;
    private final YamlConfiguration config;

    public ConfigSection(ConfigFile file, String path, YamlConfiguration config) {
        this.file = file;
        this.parentPath = path.isEmpty() ? "" : path + ".";
        this.config = config;
    }

    public boolean contains(String path) {
        return config.contains(this.parentPath + path);
    }

    /**
     * Gets a value from the configuration with type conversion.
     * Supports primitive types, enums, UUIDs, and custom type adapters.
     *
     * @param path              Path to the configuration value
     * @param type              The expected class type
     * @param defaultValue      Default value if not found or conversion fails
     * @param ignoreNonexistent Whether to ignore missing values
     * @param optionalTypeArgs  Optional arguments for type conversion
     * @param <T>               The expected return type
     * @return Value if present and convertible, otherwise defaultValue
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type, T defaultValue, boolean ignoreNonexistent, Object... optionalTypeArgs) {
        if (!verifyExists(path, ignoreNonexistent)) return defaultValue;

        if (type == Object.class) return (T) config.get(this.parentPath + path);

        if (type == String.class) {
            return (T) config.getString(this.parentPath + path);
        }
        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(config.getInt(this.parentPath + path));
        }
        if (type == Boolean.class || type == boolean.class) {
            return (T) Boolean.valueOf(config.getBoolean(this.parentPath + path));
        }
        if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(config.getDouble(this.parentPath + path));
        }
        if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(config.getLong(this.parentPath + path));
        }
        if (type == Float.class || type == float.class) {
            return (T) Float.valueOf((float) config.getDouble(this.parentPath + path));
        }
        if (type == Short.class || type == short.class) {
            return (T) Short.valueOf((short) config.getInt(this.parentPath + path));
        }
        if (type == XMaterial.class) {
            return (T) XMaterial.matchXMaterial(config.getString(this.parentPath + path)).orElse((XMaterial) defaultValue);
        }

        if (type.isEnum()) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;
                return (T) Enum.valueOf((Class<? extends Enum>) enumClass,
                        config.getString(this.parentPath + path).toUpperCase());
            } catch (Exception e) {
                return defaultValue;
            }
        }

        if (type == UUID.class) {
            try {
                return (T) UUID.fromString(config.getString(this.parentPath + path));
            } catch (Exception e) {
                return defaultValue;
            }
        }

        if (OfflinePlayer.class.isAssignableFrom(type)) {
            return (T) Bukkit.getOfflinePlayer(config.getString(this.parentPath + path));
        }

        if (World.class.isAssignableFrom(type)) {
            return (T) Bukkit.getWorld(config.getString(this.parentPath + path));
        }

        ConfigTypeAdapter<T> adapter =
                (ConfigTypeAdapter<T>) XG7PluginsAPI.configManager(file.getPlugin()).getAdapters().get(type);

        if (adapter == null) {
            file.getPlugin().getDebug().warn("Adapter not found for " + type.getName());
            return defaultValue;
        }

        T value = adapter.fromConfig(this, this.parentPath + path, optionalTypeArgs);
        return value != null ? value : defaultValue;
    }


    /**
     * Verifies if a configuration path exists and has a non-null value.
     * Optionally logs warnings if the path is missing or empty.
     *
     * @param path              Path to verify in the configuration
     * @param ignoreNonexistent Whether to suppress warning messages
     * @return true if the path exists and has value, false otherwise
     */
    private boolean verifyExists(String path, boolean ignoreNonexistent) {
        if (!contains(path)) {
            if (!ignoreNonexistent) file.getPlugin().getLogger().warning(this.parentPath + path + " not found in " + file.getName() + ".yml");
            return false;
        }
        if (config.get(this.parentPath + path) == null) {
            if (!ignoreNonexistent) file.getPlugin().getLogger().warning(this.parentPath + path + " in " + file.getName() + " is empty");
            return false;
        }
        return true;
    }

    public <T> T get(String path, T defaultValue, Object... optionalTypeArgs) {
        return get(path, (Class<T>) defaultValue.getClass(), defaultValue, true, optionalTypeArgs);
    }

    public <T> T get(String path, Class<T> type, Object... optionalTypeArgs) {
        return get(path, type, null, true, optionalTypeArgs);
    }
    public <T> T get(String path) {
        return (T) get(path, Object.class);
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

        if (type == String.class) return Optional.of((List<T>) config.getStringList(this.parentPath + path));
        if (type == Integer.class || type == int.class) return Optional.of((List<T>) config.getIntegerList(this.parentPath + path));
        if (type == Boolean.class || type == boolean.class) return Optional.of((List<T>) config.getBooleanList(this.parentPath + path));
        if (type == Double.class || type == double.class) return Optional.of((List<T>) config.getDoubleList(this.parentPath + path));
        if (type == Long.class || type == long.class) return Optional.of((List<T>) config.getLongList(this.parentPath + path));
        if (type == Float.class || type == float.class) return Optional.of((List<T>) config.getFloatList(this.parentPath + path));
        if (type == Map.class) return Optional.of((List<T>) config.getMapList(this.parentPath + path));
        if (type == Short.class || type == short.class) return Optional.of((List<T>) config.getShortList(this.parentPath + path));
        if (type.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;

            List<String> enumValues = config.getStringList(this.parentPath + path);

            List<T> enumList = new ArrayList<>();

            for (String enumValue : enumValues) {
                T enumConstant = (T) Enum.valueOf((Class) enumClass, enumValue);
                enumList.add(enumConstant);
            }
            return Optional.of(enumList);
        }

        return Optional.empty();
    }

    public <T> Optional<List<T>> getList(String path, Class<T> type) {
        return getList(path,type, true);
    }



    /**
     * Gets a time duration value from the configuration.
     * Converts a string time format to milliseconds.
     *
     * @param path              Path to the time value
     * @param ignoreNonexistent Whether to ignore missing values
     * @return Optional containing the time in milliseconds if valid
     */
    @NotNull
    public Time getTimeOrDefault(String path, Time defaultTime, boolean ignoreNonexistent) {
        String time = config.getString(this.parentPath + path);
        if (time == null) {
            if (!ignoreNonexistent) file.getPlugin().getDebug().warn(this.parentPath + path + " not found in " + file.getName() + ".yml");
            return defaultTime;
        }
        long milliseconds;
        try {
            milliseconds = TimeParser.convertToMilliseconds(time);
        } catch (TimeParser.TimeParseException e) {
            throw new RuntimeException(e);
        }
        return milliseconds == 0 ? defaultTime : Time.of(milliseconds);
    }

    @NotNull
    public Time getTimeOrDefault(String path, Time defaultTime) {
        return getTimeOrDefault(path, defaultTime, true);
    }
    
    public Time getTime(String path) {
        return getTimeOrDefault(path,Time.of(0));
    }

    public Long getTimeInMilliseconds(String path, Long defaultValue) {
        return getTimeOrDefault(path, Time.of(defaultValue)).toMilliseconds();
    }

    public Long getTimeInMilliseconds(String path) {
        return getTime(path).toMilliseconds();
    }

    public Long getTimeInTicks(String path, Long defaultValue) {
        return getTimeOrDefault(path, Time.of(defaultValue)).toTicks();
    }
    public Long getTimeInTicks(String path) {
        return getTime(path).toTicks();
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

    public Set<String> getKeys(boolean deep) {

        String realPath = parentPath.isEmpty() ? "" : parentPath.substring(0, parentPath.lastIndexOf("."));

        return config.getConfigurationSection(realPath).getKeys(deep);
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
        return (boolean) config.getClass().getMethod("is" + type.getSimpleName(), String.class).invoke(config, this.parentPath + path);
    }

    public boolean exists() {
        return file.exists() && config.contains(parentPath);
    }

    public ConfigSection parent() {
        return parentPath.contains(".") ? file.section(parentPath.substring(0, parentPath.lastIndexOf("."))) : file.root();
    }
    public ConfigSection child(String path) {
        return file.section(this.parentPath + path);
    }

}
