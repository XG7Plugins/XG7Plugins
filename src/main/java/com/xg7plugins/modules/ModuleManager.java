package com.xg7plugins.modules;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.managers.Manager;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ModuleManager implements Manager {

    private final HashMap<String, Module> extensions = new HashMap<>();
    private final Plugin plugin;

    public ModuleManager(Module... extensions) {
        this.plugin = XG7Plugins.getInstance();

        for (Module extension : extensions) {
            this.extensions.put(extension.getName(), extension);
        }

        initModules();
        loadTasks();
        loadExecutors();
        loadListeners();
    }

    public void initModules() {
        extensions.values().forEach(Module::onInit);
    }

    public void loadTasks() {
        extensions.values().forEach(extension -> XG7PluginsAPI.taskManager().registerTasks(extension.loadTasks()));
    }
    public void loadExecutors() {
        extensions.values().forEach(extension -> extension.getExecutors().forEach(XG7PluginsAPI.taskManager()::registerExecutor));
    }

    public void loadListeners() {
        extensions.values().forEach(extension -> {
            List<Listener> listeners = extension.loadListeners();

            XG7PluginsAPI.packetEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
            XG7PluginsAPI.eventManager().registerListeners(XG7Plugins.getInstance(), listeners);

        });
    }

    public void disableModules() {
        extensions.values().forEach(Module::onDisable);
    }

}
