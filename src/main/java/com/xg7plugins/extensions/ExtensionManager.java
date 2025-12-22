package com.xg7plugins.extensions;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.utils.FileUtil;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the loading, enabling, disabling, and reloading of extensions for a plugin.
 * It handles the registration of commands, listeners, and timer tasks associated with each extension.
 */
@Data
public class ExtensionManager {

    private final Plugin plugin;

    private final Map<String, Extension> extensions = new HashMap<>();
    private final Map<String, ExtensionAPI> extensionAPIs = new HashMap<>();

    /**
     * Loads extensions from the "extensions" folder within the plugin's data directory.
     * It scans for JAR files, loads the Extension classes, and initializes them.
     */
    public void loadExtensions() {
        File extensionsFolder = new File(plugin.getJavaPlugin().getDataFolder(), "extensions");

        if (!extensionsFolder.exists()) {
            extensionsFolder.mkdirs();
            return;
        }

        File[] files = extensionsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return;

        for (File file : files) {
            if (!file.isFile()) continue;
            if (!file.getName().endsWith(".jar")) continue;
            try {
                Class<? extends Extension> extensionClass = FileUtil.findClass(file, Extension.class);
                if (extensionClass == null) {
                    plugin.getDebug().warn("extension", "No Extension class found in file: " + file.getName());
                    continue;
                }

                Extension extension = extensionClass.getDeclaredConstructor().newInstance();

                if (extensions.containsKey(extension.getName())) {
                    plugin.getDebug().warn("extension", "Extension already loaded: " + extension.getName());
                    continue;
                }

                if (!extension.getPlugin().equals(plugin)) {
                    plugin.getDebug().warn("extension", "Extension " + extension.getName() + " does not belong to this plugin.");
                    continue;
                }

                extensions.put(extension.getName(), extension);
                plugin.getDebug().info("extension", "Loaded extension: " + extension.getName());

                this.extensions.put(extension.getName(), extension);
            } catch (Exception e) {
                plugin.getDebug().severe("Failed to load extension from file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Enables all loaded extensions by calling their onEnable method,
     * loading their tasks, listeners, and commands, and storing their APIs.
     */
    public void enableExtensions() {
        for (Extension extension : extensions.values()) {
            try {
                extension.onEnable();

                loadTasks(extension);
                loadListeners(extension);
                loadCommands(extension);
                extensionAPIs.put(extension.getName(), extension.getAPI());

                plugin.getDebug().info("extension", "Enabled extension: " + extension.getName());
            } catch (Exception e) {
                plugin.getDebug().severe("Failed to enable extension: " + extension.getName());
                e.printStackTrace();
            }
        }
    }

    public void reloadExtensions() {
        for (Extension extension : extensions.values()) {
            try {

                XG7Plugins.getAPI().eventManager().unregisterListeners(plugin);
                XG7Plugins.getAPI().packetEventManager().unregisterListeners(plugin);

                extension.getTimerTasks().forEach(task -> XG7Plugins.getAPI().taskManager().deleteRepeatingTask(task));
                loadTasks(extension);

                loadListeners(extension);
                extension.onReload();
                plugin.getDebug().info("extension", "Reloaded extension: " + extension.getName());
            } catch (Exception e) {
                plugin.getDebug().severe("Failed to reload extension: " + extension.getName());
                e.printStackTrace();
            }
        }
    }

    public void disableExtensions() {
        for (Extension extension : extensions.values()) {
            try {
                extension.onDisable();

                extension.getTimerTasks().forEach(task -> XG7Plugins.getAPI().taskManager().deleteRepeatingTask(task));

                plugin.getDebug().info("extension", "Disabled extension: " + extension.getName());
            } catch (Exception e) {
                plugin.getDebug().severe("Failed to disable extension: " + extension.getName());
                e.printStackTrace();
            }
        }
    }

    public boolean isExtensionLoaded(String name) {
        Extension extension = extensions.get(name);
        return extension != null;
    }

    public void loadTasks(Extension extension) {
        XG7Plugins.getAPI().taskManager().registerTimerTasks(extension.getTimerTasks());
    }

    public void loadCommands(Extension extension) {

        List<Command> commands = extension.getCommands();
        if (commands == null) return;

        ConfigSection configSection = ConfigFile.of("commands", plugin).root();

        boolean changed = false;

        int configVersion = configSection.get("config-version", -1);

        configSection.remove("config-version");

        for (Command command : commands) {
            if (!configSection.contains(command.getCommandSetup().name())) {
                changed = true;
                configSection.set(command.getCommandSetup().name(), Collections.singletonList(command.getCommandSetup().name()));
            }
        }

        configSection.set("config-version", configVersion);

        if (changed) configSection.getFile().save();

        XG7Plugins.getAPI().commandManager(plugin).registerCommands(commands);
    }

    public void loadListeners(Extension extension) {
        List<Listener> listeners = extension.getListeners();

        XG7Plugins.getAPI().packetEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
        XG7Plugins.getAPI().eventManager().registerListeners(XG7Plugins.getInstance(), listeners);
    }


    public Extension getExtension(String name) {
        return extensions.get(name);
    }
    public <T extends ExtensionAPI> T getExtensionAPI(String name) {
        return (T) extensionAPIs.get(name);
    }


}
