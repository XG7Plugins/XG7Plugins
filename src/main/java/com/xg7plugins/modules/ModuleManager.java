package com.xg7plugins.modules;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ModuleManager {

    private final HashMap<String, Module> extensions = new HashMap<>();
    private final Plugin plugin;

    public ModuleManager(Module... extensions) {
        this.plugin = XG7Plugins.getInstance();

        for (Module extension : extensions) {
            this.extensions.put(extension.getName(), extension);
        }

    }

    public void initModules() {
        extensions.values().forEach(Module::onInit);
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

            XG7Plugins.getInstance().getPacketEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
            XG7Plugins.getInstance().getEventManager().registerListeners(XG7Plugins.getInstance(), listeners);

        });
    }

    public Module getExtension(String name) {
        return extensions.get(name);
    }

    public void disableExtensions() {
        extensions.values().forEach(Module::onDisable);
    }

    public boolean isExtensionLoaded(String name) {

        if (!extensions.containsKey(name)) {
            plugin.getDebug().warn("extensions", "Extension " + name + " is not loaded. Download it from the plugin page to get more resources.");
            return false;
        }

        return true;
    }

    public void unloadExtension(String name) {
        Module extension = extensions.get(name);
        if (extension != null) {
            extension.onDisable();
            extensions.remove(name);
        }
    }

}
