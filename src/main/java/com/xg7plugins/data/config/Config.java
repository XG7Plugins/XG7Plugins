package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Optional;

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
        this.name = null;
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
        if (config.get(path) == null) {
            plugin.getLog().warn(path + " not found in " + name + ".yml");
            return Optional.empty();
        }

        if (type == String.class) return Optional.ofNullable(type.cast(config.getString(path)));
        if (type == Integer.class || type == int.class) return Optional.ofNullable(type.cast(config.contains(path) ? config.getInt(path) : null));
        if (type == Boolean.class || type == boolean.class) return Optional.ofNullable(type.cast(config.contains(path) ? config.getBoolean(path) : null));
        if (type == Double.class || type == double.class) return Optional.ofNullable(type.cast(config.contains(path) ? config.getDouble(path) : null));
        if (type == Long.class || type == long.class) return Optional.ofNullable(type.cast(config.contains(path) ? config.getLong(path) : null));
        if (type == List.class) return Optional.ofNullable(type.cast(config.getList(path)));
        if (type == ConfigurationSection.class) return Optional.ofNullable(type.cast(config.getConfigurationSection(path)));
        if (type.isEnum()) return Optional.ofNullable(config.contains(path) ? type.cast(Enum.valueOf((Class<? extends Enum>) type, config.getString(path).toUpperCase())) : null);

        ConfigTypeAdapter<T> adapter = (ConfigTypeAdapter<T>) configManager.getAdapters().get(type);

        if (adapter == null) {
            plugin.getLog().warn("Adapter not found for " + type.getName());
            return Optional.empty();
        }

        return Optional.ofNullable(adapter.fromConfig(get(path, ConfigurationSection.class).orElse(null), optionalTypeArgs));
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
