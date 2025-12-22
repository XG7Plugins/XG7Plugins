package com.xg7plugins.modules;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;

import com.xg7plugins.utils.PluginKey;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the lifecycle and initialization of modules in the plugin system.
 * Handles registration of tasks, executors, and event listeners for each module.
 */
@Getter
public class ModuleManager {

    private final HashMap<String, Module> modules = new HashMap<>();
    private final Plugin plugin;

    public ModuleManager() {
        this.plugin = XG7Plugins.getInstance();
    }

    public void registerModules(Module... extensions) {
        for (Module extension : extensions) {
            this.modules.put(extension.getName(), extension);
        }
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

    /**
     * Disables a specific module by calling its onDisable method and unregistering its tasks and executors.
     * @param module The module to disable
     */
    public void disableModule(Module module) {
        module.loadTasks().forEach(task -> XG7Plugins.getAPI().taskManager().deleteRepeatingTask(task));
        module.getExecutors().forEach((n, e) -> XG7Plugins.getAPI().taskManager().removeExecutor(n));

        module.onDisable();
        module.setEnabled(false);
    }

    /**
     * Reloads a specific module by calling its onReload method.
     * @param module The module to reload
     */
    public void reloadModule(Module module) {
        module.onReload();
    }

    /**
     * Loads and registers all tasks from the registered modules.
     */
    public void loadTasks(Module module) {
        XG7Plugins.getAPI().taskManager().registerTimerTasks(module.loadTasks());
    }

    /**
     * Loads and registers all executors from the registered modules.
     */
    public void loadExecutors(Module module) {
        module.getExecutors().forEach(XG7Plugins.getAPI().taskManager()::registerExecutor);
    }

    /**
     * Loads and registers all event listeners from the registered modules.
     * Handles both packet listeners and regular event listeners.
     */
    public void loadListeners(Module module) {
        List<Listener> listeners = module.loadListeners();

        XG7Plugins.getAPI().packetEventManager().registerListeners(XG7Plugins.getInstance(), listeners.stream().filter(l -> l instanceof PacketListener).map(l -> (PacketListener) l).collect(Collectors.toList()));
        XG7Plugins.getAPI().eventManager().registerListeners(XG7Plugins.getInstance(), listeners);
    }

    /**
     * Checks if a specific module is enabled.
     * @param moduleName The name of the module to check
     * @return True if the module is enabled, false otherwise
     */
    public boolean isModuleEnabled(String moduleName) {
        return modules.containsKey(moduleName) &&  modules.get(moduleName).isEnabled();
    }

    /**
     * Disables all registered modules by calling them onDisable method.
     */
    public void disableAllModules() {
        modules.values().forEach(this::disableModule);
    }

    /**
     * Reloads all registered modules by calling them onReload method.
     */
    public void reloadAllModules() {
        modules.values().forEach(this::reloadModule);
    }

    /**
     * Gets a specific module by its name.
     * @param moduleName The name of the module to retrieve
     * @return The module instance
     * @param <T> The type of the module
     */
    public <T extends Module> T getModule(String moduleName) {
        return (T) modules.get(moduleName);
    }

    /**
     * Gets a specific module by its class type.
     * @param moduleClass The class type of the module to retrieve
     * @return The module instance
     * @param <T> The type of the module
     */
    public <T extends Module> T getModule(Class<T> moduleClass) {
        return modules.values().stream().filter(moduleClass::isInstance).map(moduleClass::cast).findFirst().orElse(null);
    }

}
