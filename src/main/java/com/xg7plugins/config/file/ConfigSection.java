package com.xg7plugins.config.file;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.utils.time.TimeParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration manager class that handles YAML configuration files.
 * Provides methods for loading, saving, and accessing configuration data
 * with automatic version control and backup functionality.
 */
@Getter
public class ConfigSection {

    private final ConfigFile file;
    @Getter(AccessLevel.NONE)
    private final String currentPath;
    private final Map<String, Object> rootData;
    private final Map<String, Object> data;

    public ConfigSection(ConfigFile file, String path, Map<String, Object> data) {
        this.file = file;
        this.currentPath = path;
        this.rootData = data;
        this.data = getDataOfCurrent();
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

        if (path.isEmpty()) {
            T value = getAs("", type, optionalTypeArgs);

            return value != null ? value : defaultValue;
        }

        if (!verifyExists(path, ignoreNonexistent)) return defaultValue;

        Object object = getByPath(path);

        if (
                type == Object.class || type == String.class ||
                type == Boolean.class || type == boolean.class ||
                type == Map.class

        ) {
            return (T) object;
        }

        if (
                type == Integer.class || type == int.class ||
                        type == Double.class || type == double.class ||
                        type == Long.class || type == long.class ||
                        type == Float.class || type == float.class ||
                        type == Short.class || type == short.class
        ) {

            if (object instanceof Integer && !(type == Integer.class || type == int.class)) {
                Number number = (Integer) object;
                if (type == Double.class || type == double.class) {
                    return (T) (Double) number.doubleValue();
                }
                if (type == Long.class || type == long.class) {
                    return (T) (Long) number.longValue();
                }
                if (type == Float.class || type == float.class) {
                    return (T) (Float) number.floatValue();
                }
                return (T) (Short) number.shortValue();
            }

            return (T) object;
        }

        if (List.class.isAssignableFrom(type)) {
            return (T) getList(path, Object.class).orElse(new ArrayList<>());
        }

        String objectAsString = object.toString();

        if (type == XMaterial.class) {
            return (T) XMaterial.matchXMaterial(objectAsString).orElse((XMaterial) defaultValue);
        }

        if (type.isEnum()) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;
                return (T) Enum.valueOf((Class<? extends Enum>) enumClass, objectAsString.toUpperCase());
            } catch (Exception e) {
                return defaultValue;
            }
        }

        if (type == UUID.class) {
            try {
                return (T) UUID.fromString(objectAsString);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        if (type == Time.class) {
            return (T) getTime(path);
        }

        if (OfflinePlayer.class.isAssignableFrom(type)) {
            return (T) Bukkit.getOfflinePlayer(objectAsString);
        }

        if (World.class.isAssignableFrom(type)) {
            return (T) Bukkit.getWorld(objectAsString);
        }

        T value = getAs(path, type, optionalTypeArgs);

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
            if (!ignoreNonexistent) file.getPlugin().getJavaPlugin().getLogger().warning(this.currentPath + path + " not found in " + file.getName() + ".yml");
            return false;
        }
        if (getByPath(path) == null) {
            if (!ignoreNonexistent) file.getPlugin().getJavaPlugin().getLogger().warning(this.currentPath + path + " in " + file.getName() + " is empty");
            return false;
        }
        return true;
    }

    /**
     * Gets a value from the configuration with type conversion and a default value.
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path does not exist
     * @param optionalTypeArgs Optional arguments for type conversion
     * @return The value at the specified path, or the default value if not found
     * @param <T> The expected return type
     */
    public <T> T get(String path, T defaultValue, Object... optionalTypeArgs) {
        return get(path, (Class<T>) defaultValue.getClass(), defaultValue, true, optionalTypeArgs);
    }

    /**
     * Gets a value from the configuration with type conversion.
     * @param path The path to the configuration value
     * @param optionalTypeArgs Optional arguments for type conversion
     * @return The value at the specified path, or the default value if not found
     * @param <T> The expected return type

     */
    public <T> T get(String path, Class<T> type, Object... optionalTypeArgs) {
        return get(path, type, null, true, optionalTypeArgs);
    }

    /**
     * Gets a value from the configuration without type conversion.
     * @param path The path to the configuration value
     * @return The value at the specified path
     * @param <T> The expected return type
     */
    public <T> T get(String path) {
        return (T) get(path, Object.class);
    }

    @SuppressWarnings("unchecked")
    private  <T> T getByPath(String path) {
        if (path == null || path.isEmpty() || !exists()) return null;

        String[] parts = path.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length; i++) {
            String key = parts[i];
            Object value = current.get(key);

            if (i == parts.length - 1) {
                return (T) value;
            }

            if (!(value instanceof Map)) {
                return null;
            }

            current = (Map<String, Object>) value;
        }

        return null;
    }

    /**
     * Gets the type of the value associated with the given key in the configuration.
     *
     * @param key The key to check the type for
     * @return The Class representing the type of the value
     */
    public Class<?> getType(String key) {
        Object object = getByPath(key);
        String objectAsString = object.toString();

        if (TimeParser.isTime(objectAsString)) {
            return Time.class;
        }

        try {
            UUID.fromString(objectAsString);
            return UUID.class;
        } catch (Exception ignored) {}

        return object.getClass();
    }

    /**
     * Gets a value from the configuration with type conversion using a type adapter.
     *
     * @param path The path to the configuration value
     * @param type The expected class type
     * @param optionalTypeArgs Optional arguments for type conversion
     * @return The value at the specified path, converted to the expected type
     * @param <T> The expected return type
     */
    public <T> T getAs(String path, Class<T> type, Object... optionalTypeArgs) {

        ConfigTypeAdapter<T> adapter = (ConfigTypeAdapter<T>) XG7Plugins.getAPI().configManager(file.getPlugin()).getAdapters().get(type);

        if (adapter != null) {
            return adapter.fromConfig(this, path, optionalTypeArgs);
        }

        Yaml yaml = new Yaml();
        String dumped = yaml.dump(path.isEmpty() ? this.data : get(path));
        return yaml.loadAs(dumped, type);
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

        List<Object> list = getByPath(path);

        if (
                type == Object.class || type == String.class || type == Integer.class || type == int.class ||
                        type == Boolean.class || type == boolean.class || type == Double.class || type == double.class ||
                        type == Long.class || type == long.class || type == Float.class || type == float.class ||
                        type == Short.class || type == short.class || type == Map.class
        ) {
            return Optional.of((List<T>) list);
        }

        if (type.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;

            List<String> enumValues = list.stream().map(Object::toString).collect(Collectors.toList());

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
     * Gets the current path of this configuration section.
     * @return The current path as a string
     */
    public String getPath() {
        return currentPath;
    }

    /**
     * @return the last path -> config.section -> section
     * @deprecated use #getPath() instead
     */
    @Deprecated
    public String getName() {
        String path = getPath();
        if (path.isEmpty()) return ""; // root
        int lastDot = path.lastIndexOf(".");
        return lastDot == -1 ? path : path.substring(lastDot + 1);
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
        String time = data.get(path) != null ? data.get(path).toString() : null;
        if (time == null) {
            if (!ignoreNonexistent) file.getPlugin().getDebug().warn("config", this.currentPath + path + " not found in " + file.getName() + ".yml");
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

    /**
     * Gets a time duration value from the configuration.
     * Converts a string time format to milliseconds.
     *
     * @param path Path to the time value
     * @param defaultTime The default time to return if the path does not exist
     * @return The time value at the specified path, or the default time if not found
     */
    @NotNull
    public Time getTimeOrDefault(String path, Time defaultTime) {
        return getTimeOrDefault(path, defaultTime, true);
    }

    /**
     * Gets a time duration value from the configuration.
     * @param path Path to the time value
     * @return The time value at the specified path
     */
    @NotNull
    public Time getTime(String path) {
        return getTimeOrDefault(path,Time.of(0));
    }

    /**
     * Gets a time duration value from the configuration in milliseconds.
     * @param path The path to the time value
     * @param defaultValue The default value in milliseconds if the path does not exist
     * @return The time value at the specified path in milliseconds, or the default value if not found
     */
    public Long getTimeInMilliseconds(String path, Long defaultValue) {
        return getTimeOrDefault(path, Time.of(defaultValue)).toMilliseconds();
    }

    /**
     * Gets a time duration value from the configuration in milliseconds.
     * @param path The path to the time value
     * @return The time value at the specified path in milliseconds
     */
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
        data.put(path, value);
    }

    /**
     * Removes a value from the configuration at the specified path.
     * @param path The path of the value to remove
     */
    public void remove(String path) {
        data.remove(path);
    }

    /**
     * Gets all keys in the configuration section.
     *
     * @param deep Whether to include keys from nested sections
     * @return A set of all keys in the configuration section
     */
    @SuppressWarnings("unchecked")
    public Set<String> getKeys(boolean deep) {
        if (!exists()) return Collections.emptySet();

        Set<String> keys = new LinkedHashSet<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            keys.add(key);

            if (deep && entry.getValue() instanceof Map) {
                addDeepKeys(key, (Map<String, Object>) entry.getValue(), keys);
            }
        }

        return keys;
    }

    /**
     * Checks if the configuration contains a value at the specified path.
     * @param path The path to check
     * @return true if the path exists, false otherwise
     */
    public boolean contains(String path) {
        if (path == null || path.isEmpty() || !exists()) return false;

        String[] parts = path.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length; i++) {
            String key = parts[i];
            Object value = current.get(key);

            if (i == parts.length - 1 && current.containsKey(key)) {
                return true;
            }

            if (!(value instanceof Map)) {
                return false;
            }

            current = (Map<String, Object>) value;

        }

        return true;
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
        return data.get(path) != null && type.isAssignableFrom(data.get(path).getClass());
    }

    /**
     * Checks if the configuration section exists.
     * @return true if the section exists, false otherwise
     */
    public boolean exists() {
        return data != null && file.exists();
    }

    /**
     * Gets the parent configuration section.
     * @return The parent configuration section
     */
    public ConfigSection parent() {
        return currentPath.contains(".") ? file.section(currentPath.substring(0, currentPath.lastIndexOf("."))) : file.root();
    }

    /**
     * Gets a child configuration section at the specified path.
     * @param path The path to the child section
     * @return The child configuration section
     */
    public ConfigSection child(String path) {
        return currentPath.isEmpty() ? file.section(path) : file.section(this.currentPath + "." + path);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataOfCurrent() {
        if (currentPath == null || currentPath.isEmpty()) return rootData;

        String[] parts = currentPath.split("\\.");
        Map<String, Object> current = rootData;

        for (String part : parts) {
            Object next = current.get(part);

            if (!(next instanceof Map)) {
                return null;
            }

            current = (Map<String, Object>) next;
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private void addDeepKeys(String parent, Map<String, Object> map, Set<String> keys) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = parent + "." + entry.getKey();
            keys.add(key);

            if (entry.getValue() instanceof Map) {
                addDeepKeys(key, (Map<String, Object>) entry.getValue(), keys);
            }
        }
    }

}
