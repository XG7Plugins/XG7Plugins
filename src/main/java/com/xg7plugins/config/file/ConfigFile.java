package com.xg7plugins.config.file;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.ConfigManager;
import com.xg7plugins.utils.FileUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class ConfigFile {

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
    public ConfigFile(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        if (plugin.getDebug() != null) plugin.getDebug().info("Loading " + name + ".yml...");
        else plugin.getLogger().info("Loading " + name + ".yml...");

        File configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (plugin.getResource(name + ".yml") != null) {

            YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(FileUtil.reader(FileUtil.fromResource(plugin, name + ".yml")));
            if (!resourceConfig.getString("config-version").equals(config.getString("config-version"))) {

                File backupFile = new File(plugin.getDataFolder(), name + "-old.yml");
                configFile.renameTo(backupFile);

                plugin.saveResource(name + ".yml", true);

                this.configFile = new File(plugin.getDataFolder(), name + ".yml");

                this.config = YamlConfiguration.loadConfiguration(configFile);

                plugin.getLogger().info("Loaded!");

                return;
            }

        }

        this.configFile = configFile;
        this.config = config;

        if (plugin.getManagerRegistry().getManager(ConfigManager.class) != null)
            plugin.getManagerRegistry().getManager(ConfigManager.class).putConfig(this);
    }

    public ConfigFile(Plugin plugin, YamlConfiguration config, String name, boolean createFile) throws IOException {
        this.plugin = plugin;
        this.name = name;
        this.config = config;
        this.configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists() && createFile) configFile.createNewFile();
    }

    /**
     * Creates a new Config instance with an existing YamlConfiguration.
     *
     * @param plugin The plugin instance
     * @param config The existing YamlConfiguration to use
     * @param name   The name of the configuration file without extension
     * @throws IOException if unable to create the config file
     */
    public ConfigFile(Plugin plugin, YamlConfiguration config, String name) throws IOException {
        this(plugin,config,name,true);
    }

    /**
     * Factory method to get or create a Config instance.
     * Returns existing config if available, otherwise creates new one.
     *
     * @param name   The name of the configuration file
     * @param plugin The plugin instance
     * @return The Config instance
     */
    public static ConfigFile of(String name, Plugin plugin) {
        if (XG7PluginsAPI.configManager(plugin).getConfigs().containsKey(name)) {
            return XG7PluginsAPI.configManager(plugin).getConfigs().get(name);
        }
        return new ConfigFile(plugin, name);
    }

    /**
     * Gets or creates the main configuration file for a plugin.
     *
     * @param plugin The plugin instance
     * @return The main Config instance
     */
    public static ConfigFile mainConfigOf(Plugin plugin) {
        return ConfigFile.of("config", plugin);
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
     * Reloads the configuration from the file.
     * Updates the config manager with the reloaded instance.
     */
    public void reload() {
        plugin.getDebug().info("Reloading " + name + ".yml...");

        this.config = YamlConfiguration.loadConfiguration(configFile);

        XG7PluginsAPI.configManager(plugin).putConfig(this);

        plugin.getDebug().info("Reloaded");
    }

    public boolean exists() {
        return configFile.exists();
    }

    public ConfigSection section(String path) {
        return new ConfigSection(this, path, config);
    }

    public ConfigSection root() {
        return section("");
    }

}
