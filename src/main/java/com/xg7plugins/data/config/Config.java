package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Config {

    private final Plugin plugin;
    private final String name;
    private final ConfigManager configManager;
    private YamlConfiguration config;

    @SneakyThrows
    public Config(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.configManager = plugin.getConfigsManager();

        plugin.getLogger().info("Loading " + name + ".yml...");

        File configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);

        this.config = YamlConfiguration.loadConfiguration(configFile);

        plugin.getLogger().info("Loaded!");
    }

    public Config(Plugin plugin, YamlConfiguration config) {
        this.plugin = plugin;
        this.name = config.getName();
        this.configManager = plugin.getConfigsManager();
        this.config = config;
    }

    public static Config of(Plugin plugin, YamlConfiguration config) {
        return new Config(plugin, config);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public <T> Optional<T> get(String path, Class<T> type, Object... optionalTypeArgs) {
        if (!config.contains(path)) {
            plugin.getLog().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }
        if (config.get(path) == null) {
            plugin.getLog().warn(path + " in " + name + " is empty");
            return Optional.empty();
        }

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
            plugin.getLog().warn("Adapter not found for " + type.getName());
            return Optional.empty();
        }

        return Optional.ofNullable(adapter.fromConfig(get(path, ConfigurationSection.class).orElse(null), optionalTypeArgs));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> getList(String path, Class<T> type) {
        if (!config.contains(path)) {
            plugin.getLog().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }
        if (config.get(path) == null) {
            plugin.getLog().warn(path + " in " + name + " is empty");
            return Optional.empty();
        }

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

    public Optional<Long> getTime(String path) {
        String time = config.getString(path);
        if (time == null) {
            plugin.getLog().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }
        long milliseconds = Text.convertToMilliseconds(plugin, time);
        return Optional.ofNullable(milliseconds == 0 ? null : milliseconds);
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
        plugin.getLog().info("Saving " + name + ".yml...");
        config.save(new File(plugin.getDataFolder(), name + ".yml"));
        plugin.getLog().info("Saved!");
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), name + ".yml");

        plugin.getLog().loading("Reloading " + name + ".yml...");

        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getConfigsManager().getConfigs().put(name,this);

        plugin.getLog().loading("Reloaded!");
    }

}
