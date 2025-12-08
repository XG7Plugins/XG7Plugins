package com.xg7plugins.config.file;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.XG7Plugins;
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
        if (!verifyExists(path, ignoreNonexistent)) return defaultValue;

        Object object = data.get(path);

        if (
                type == Object.class || type == String.class || type == Integer.class || type == int.class ||
                type == Boolean.class || type == boolean.class || type == Double.class || type == double.class ||
                type == Long.class || type == long.class || type == Float.class || type == float.class ||
                type == Short.class || type == short.class
        ) {
            return (T) object;
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

        ConfigTypeAdapter<T> adapter = (ConfigTypeAdapter<T>) XG7Plugins.getAPI().configManager(file.getPlugin()).getAdapters().get(type);

        if (adapter == null) {
            file.getPlugin().getDebug().warn("config", "Adapter not found for " + type.getName());
            return defaultValue;
        }

        T value = adapter.fromConfig(this, path, optionalTypeArgs);
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
        if (data.get(path) == null) {
            if (!ignoreNonexistent) file.getPlugin().getJavaPlugin().getLogger().warning(this.currentPath + path + " in " + file.getName() + " is empty");
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

    public <T> T getAs(Class<T> type) {
        Yaml yaml = new Yaml();
        String dumped = yaml.dump(this.data);
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

        List<Object> list = (List<Object>) data.get(path);

        if (
                type == Object.class || type == String.class || type == Integer.class || type == int.class ||
                        type == Boolean.class || type == boolean.class || type == Double.class || type == double.class ||
                        type == Long.class || type == long.class || type == Float.class || type == float.class ||
                        type == Short.class || type == short.class
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


    public String getPath() {
        return currentPath;
    }

    //Return the last path -> config.section -> section
    public String getName() {
        String path = getPath();
        if (path.isEmpty()) return ""; // raiz
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
        String time = data.get(path).toString();
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
        data.put(path, value);
    }

    public void remove(String path) {
        data.remove(path);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getKeys(boolean deep) {
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


    public boolean contains(String path) {
        return data.containsKey(path);
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

    public boolean exists() {
        return file.exists() && data != null;
    }

    public ConfigSection parent() {
        return currentPath.contains(".") ? file.section(currentPath.substring(0, currentPath.lastIndexOf("."))) : file.root();
    }
    public ConfigSection child(String path) {
        return file.section(this.currentPath + "." + path);
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
