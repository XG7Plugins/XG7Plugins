package com.xg7plugins.modules;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.managers.Manager;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the lifecycle and initialization of modules in the plugin system.
 * Handles registration of tasks, executors, and event listeners for each module.
 */
@Getter
public class ModuleManager implements Manager {

    private final HashMap<String, Module> modules = new HashMap<>();
    private final Plugin plugin;

    /**
     * Creates a new ModuleManager and initializes the provided modules.
     *
     * @param extensions The modules to be managed
     */
    public ModuleManager(Module... extensions) {
        this.plugin = XG7Plugins.getInstance();

        for (Module extension : extensions) {
            this.modules.put(extension.getName(), extension);
        }

        ConfigSection moduleSection = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).section("modules-enabled");

        this.modules.values().stream()
                .filter(module -> moduleSection.get(module.getName().toLowerCase(), false) && module.canBeEnabled())
                .forEach(this::initModule);
    }

    /**
     * Initializes all registered modules by calling their onInit method.
     */
    public void initModule(Module module) {
        loadExecutors(module);
        loadTasks(module);
        loadListeners(module);

        module.onInit();
        module.setEnabled(true);
    }

    public void disableModule(Module module) {
        module.loadTasks().forEach(task -> XG7PluginsAPI.taskManager().deleteRepeatingTask(XG7Plugins.getInstance(), task.getId()));
        module.getExecutors().forEach((n, e) -> XG7PluginsAPI.taskManager().removeExecutor(n));

        module.onDisable();
        module.setEnabled(false);
    }

    public void reloadModule(Module module) {
        module.onReload();
    }

    /**
     * Loads and registers all tasks from the registered modules.
     */
    public void loadTasks(Module module) {
        XG7PluginsAPI.taskManager().registerTimerTasks(module.loadTasks());
    }

    /**
     * Loads and registers all executors from the registered modules.
     */
    public void loadExecutors(Module module) {
        module.getExecutors().forEach(XG7PluginsAPI.taskManager()::registerExecutor);
    }

    /**
     * Loads and registers all event listeners from the registered modules.
     * Handles both packet listeners and regular event listeners.
     */
    public void loadListeners(Module module) {
        List<Listener> listeners = module.loadListeners();

        XG7PluginsAPI.packetEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
        XG7PluginsAPI.eventManager().registerListeners(XG7Plugins.getInstance(), listeners);
    }

    public boolean isModuleEnabled(String moduleName) {
        return modules.containsKey(moduleName) &&  modules.get(moduleName).isEnabled();
    }

    /**
     * Disables all registered modules by calling them onDisable method.
     */
    public void disableAllModules() {
        modules.values().forEach(this::disableModule);
    }

    public void reloadAllModules() {
        modules.values().forEach(this::reloadModule);
    }

    public <T extends Module> T getModule(String moduleName) {
        return (T) modules.get(moduleName);
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        return modules.values().stream().filter(moduleClass::isInstance).map(moduleClass::cast).findFirst().orElse(null);
    }

}
