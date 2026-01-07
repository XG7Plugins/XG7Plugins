package com.xg7plugins.config.file;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.FileUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Getter
public class ConfigFile {

    private final Plugin plugin;
    private final String name;
    private final File configFile;

    private Map<String, Object> data;

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

        if (plugin.getDebug() != null) plugin.getDebug().info("config", "Loading " + name + ".yml...");
        else plugin.getJavaPlugin().getLogger().info("Loading " + name + ".yml...");

        File configFile = new File(plugin.getJavaPlugin().getDataFolder(), name + ".yml");

        if (!configFile.exists()) {
            try {
                plugin.getJavaPlugin().saveResource(name + ".yml", false);
            } catch (IllegalArgumentException ex) {
                if (!configFile.createNewFile()) {
                    throw new IOException(ex.getMessage());
                }
                //Doesn't exists in resources
            }
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(FileUtil.reader(configFile));

        if (plugin.getJavaPlugin().getResource(name + ".yml") != null) {

            Map<String, Object> resourceData = yaml.load(FileUtil.fromResource(plugin, name + ".yml"));

            if (resourceData.get("config-version") != null && data.get("config-version") != null && !resourceData.get("config-version").equals(data.get("config-version"))) {

                    FileUtil.renameFile(configFile,name + "-old.yml");

                    FileUtil.saveResource(plugin, name + ".yml");

                    this.configFile = new File(plugin.getJavaPlugin().getDataFolder(), name + ".yml");

                    this.data =  ConfigVersionMigration.migrate(data, resourceData);

                    save();

                    plugin.getJavaPlugin().getLogger().info("Loaded!");

                    return;
            }

        }

        this.configFile = configFile;
        this.data = data;
    }

    /**
     * Creates a new Config instance from an existing file.
     *
     * @param plugin     The plugin instance
     * @param configFile The configuration file
     * @throws IOException If the file does not exist or cannot be read
     */
    public ConfigFile(Plugin plugin, File configFile) throws IOException {
        this.configFile = configFile;
        this.plugin = plugin;
        this.name = configFile.getName().replace(".yml", "");

        if (!configFile.exists()) throw new FileNotFoundException("File doesn't exist!");

        Yaml yaml = new Yaml();
        this.data = yaml.load(FileUtil.reader(configFile));

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
        if (XG7Plugins.getAPI().configManager(plugin).getConfigs().containsKey(name)) {
            return XG7Plugins.getAPI().configManager(plugin).getConfigs().get(name);
        }
        return new ConfigFile(plugin, name);
    }

    /**
     * Factory method to create a Config instance from an existing file.
     *
     * @param file   The configuration file
     * @param plugin The plugin instance
     * @return The Config instance
     * @throws IOException If the file does not exist or cannot be read
     */
    public static ConfigFile of(File file, Plugin plugin) throws IOException {
        return new ConfigFile(plugin, file);
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
     * Converts the root of the configuration data to an instance of the specified type.
     *
     * @param type The class type to convert the root data to
     * @param <T>  The type parameter
     * @return An instance of the specified type representing the root data
     */
    public <T> T getFromRoot(Class<T> type) {
        Yaml yaml = new Yaml(new Constructor(type, new LoaderOptions()));

        String dumped = new Yaml().dump(data);

        return yaml.load(dumped);
    }

    /**
     * Saves the configuration to file.
     * Logs the save operation in debug mode.
     */
    @SneakyThrows
    public void save() {
        plugin.getDebug().info("config", "Saving " + name + ".yml...");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setIndicatorIndent(1);

        Yaml yaml = new Yaml(options);
        yaml.dump(data, new FileWriter(this.configFile));

        plugin.getDebug().info("config", "Saved!");
    }

    /**
     * Reloads the configuration from the file.
     * Updates the config manager with the reloaded instance.
     */
    public void reload() throws IOException {
        plugin.getDebug().info("load", "Reloading " + name + ".yml...");

        Yaml yaml = new Yaml();
        this.data = yaml.load(FileUtil.reader(configFile));

        XG7Plugins.getAPI().configManager(plugin).putConfig(this);

        plugin.getDebug().info("load", "Reloaded");
    }

    /**
     * Checks if the configuration file exists.
     * @return True if the file exists, false otherwise
     */
    public boolean exists() {
        return configFile.exists();
    }

    /**
     * Gets a configuration section at the specified path.
     * @param path The path to the configuration section
     * @return The ConfigSection instance at the specified path
     */
    public ConfigSection section(String path) {
        return new ConfigSection(this, path, data);
    }

    /**
     * Gets the root configuration section.
     * @return The root ConfigSection instance
     */
    public ConfigSection root() {
        return section("");
    }

}
