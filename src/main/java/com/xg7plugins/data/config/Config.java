package com.xg7plugins.data.config;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Time;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Config {

    private final Plugin plugin;
    private final String name;
    private final File configFile;
    private final ConfigManager configManager;
    private YamlConfiguration config;

    @SneakyThrows
    public Config(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configManager = XG7PluginsAPI.configManager(plugin);

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

    public Config(Plugin plugin, YamlConfiguration config, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configManager = XG7PluginsAPI.configManager(plugin);
        this.config = config;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public static Config of(String name, Plugin plugin) {
        if (XG7PluginsAPI.configManager(plugin).getConfigs().containsKey(name)) {
            return XG7PluginsAPI.configManager(plugin).getConfigs().get(name);
        }
        return new Config(plugin, name);
    }
    public static Config mainConfigOf(Plugin plugin) {
        return Config.of("config", plugin);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

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

        ConfigTypeAdapter<T> adapter = (ConfigTypeAdapter<T>) configManager.getAdapters().get(type);

        if (adapter == null) {
            plugin.getDebug().warn("Adapter not found for " + type.getName());
            return Optional.empty();
        }

        return Optional.ofNullable(adapter.fromConfig(get(path, ConfigurationSection.class).orElse(null), optionalTypeArgs));
    }

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


    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> getList(String path, Class<T> type, boolean ignoreNonexistent) {
        if (verifyExists(path, ignoreNonexistent)) return Optional.empty();

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

    public Optional<Long> getTime(String path, boolean ignoreNonexistent) {
        String time = config.getString(path);
        if (time == null) {
            if (!ignoreNonexistent) plugin.getDebug().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }
        long milliseconds;
        try {
            milliseconds = Time.convertToMilliseconds(time);
        } catch (Time.TimeParseException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(milliseconds == 0 ? null : milliseconds);
    }
    public Optional<Long> getTime(String path) {
        return getTime(path,false);
    }

    public void set(String path, Object value) {
        config.set(path,value);
    }

    @SneakyThrows
    public <T> boolean is(String path, Class<T> type) {
        return (boolean) config.getClass().getMethod("is" + type.getSimpleName(), String.class).invoke(config, path);
    }

    @SneakyThrows
    public void save() {
        plugin.getDebug().info("Saving " + name + ".yml...");
        config.save(configFile);
        plugin.getDebug().info("Saved!");
    }

    public void reload() {
        plugin.getDebug().info("Reloading " + name + ".yml...");

        this.config = YamlConfiguration.loadConfiguration(configFile);

        configManager.putConfig(name,this);

        plugin.getDebug().info("Reloaded");
    }

}
