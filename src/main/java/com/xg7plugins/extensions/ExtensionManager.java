package com.xg7plugins.extensions;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ExtensionManager {

    private final File extensionsFolder = new File("extensions");
    private final HashMap<String, Extension> extensions = new HashMap<>();
    private final Plugin plugin;

    public ExtensionManager(Plugin plugin) {
        this.plugin = plugin;

        if (!extensionsFolder.exists()) extensionsFolder.mkdirs();

        File[] files = extensionsFolder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (files == null) return;

        for (File file : files) {
            try (ExtensionClassLoader loader = new ExtensionClassLoader(file, getClass().getClassLoader())) {
                Extension extension = loader.loadExtension(file);
                extension.onInit();
                extensions.put(extension.getName(), extension);
                plugin.getDebug().info("extensions", "Loaded extension " + extension.getName());
            } catch (Exception e) {
                plugin.getDebug().severe("extensions", "Failed to load extension " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void initExtensions() {
        extensions.values().forEach(Extension::onInit);
    }

    public void loadTasks() {
        extensions.values().forEach(extension -> XG7Plugins.taskManager().registerTasks(extension.loadTasks()));
    }
    public void loadExecutors() {
        extensions.values().forEach(extension -> extension.getExecutors().forEach(XG7Plugins.taskManager()::registerExecutor));
    }

    public void loadListeners() {
        extensions.values().forEach(extension -> {
            List<Listener> listeners = extension.loadListeners();

            XG7Plugins.getInstance().getPacketEventManager().registerListeners(extension.getPlugin(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
            XG7Plugins.getInstance().getEventManager().registerListeners(extension.getPlugin(), listeners);

        });
    }
    public void loadCommands() {
        extensions.values().forEach(extension -> extension.getPlugin().getCommandManager().registerCommands(extension.loadCommands().toArray(new ICommand[0])));
    }

    public Extension getExtension(String name) {
        return extensions.get(name);
    }

    public void disableExtensions() {
        extensions.values().forEach(Extension::disable);
    }

    public boolean isExtensionLoaded(String name) {
        return extensions.containsKey(name);
    }

    public void unloadExtension(String name) {
        Extension extension = extensions.get(name);
        if (extension != null) {
            extension.disable();
            extensions.remove(name);
        }
    }

    public static void requiresExtension(Plugin plugin, String name) {
        if (!plugin.getExtensionManager().isExtensionLoaded(name)) throw new ExtensionNotFoundException(plugin, name);
    }

}
