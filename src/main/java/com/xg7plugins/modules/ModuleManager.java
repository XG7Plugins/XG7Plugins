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

/**
 * Manages the lifecycle and initialization of modules in the plugin system.
 * Handles registration of tasks, executors, and event listeners for each module.
 */
@Getter
public class ModuleManager implements Manager {

    private final HashMap<String, Module> extensions = new HashMap<>();
    private final Plugin plugin;

    /**
     * Creates a new ModuleManager and initializes the provided modules.
     *
     * @param extensions The modules to be managed
     */
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

    /**
     * Initializes all registered modules by calling their onInit method.
     */
    public void initModules() {
        extensions.values().forEach(Module::onInit);
    }

    /**
     * Loads and registers all tasks from the registered modules.
     */
    public void loadTasks() {
        extensions.values().forEach(extension -> XG7PluginsAPI.taskManager().registerTimerTasks(extension.loadTasks()));
    }

    /**
     * Loads and registers all executors from the registered modules.
     */
    public void loadExecutors() {
        extensions.values().forEach(extension -> extension.getExecutors().forEach(XG7PluginsAPI.taskManager()::registerExecutor));
    }

    /**
     * Loads and registers all event listeners from the registered modules.
     * Handles both packet listeners and regular event listeners.
     */
    public void loadListeners() {
        extensions.values().forEach(extension -> {
            List<Listener> listeners = extension.loadListeners();

            XG7PluginsAPI.packetEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
            XG7PluginsAPI.eventManager().registerListeners(XG7Plugins.getInstance(), listeners);

        });
    }

    /**
     * Disables all registered modules by calling them onDisable method.
     */
    public void disableModules() {
        extensions.values().forEach(Module::onDisable);
    }

}
