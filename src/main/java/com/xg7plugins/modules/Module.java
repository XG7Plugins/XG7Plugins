package com.xg7plugins.modules;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.tasks.Task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Represents a module within the plugin system.
 * Modules are independent components that can be initialized, disabled,
 * and can provide executors, tasks, and event listeners.
 */
public interface Module {

    /**
     * Called when the module is being initialized.
     * Use this method to set up any necessary resources or configurations.
     */
    void onInit();

    /**
     * Called when the module is being disabled.
     * Use this method to clean up resources and perform shutdown operations.
     */
    void onDisable();

    /**
     * Provides a map of named executor services used by this module.
     * By default, returns an empty map.
     *
     * @return A map containing executor services with their associated names
     */
    default Map<String, ExecutorService> getExecutors() {
        return Collections.emptyMap();
    }

    /**
     * Loads and returns a list of tasks associated with this module.
     * By default, returns an empty list.
     *
     * @return A list of tasks to be executed
     */
    default List<Task> loadTasks() {
        return Collections.emptyList();
    }

    /**
     * Loads and returns a list of event listeners for this module.
     * By default, it returns an empty list.
     *
     * @return A list of event listeners to be registered
     */
    default List<Listener> loadListeners() {
        return Collections.emptyList();
    }

    /**
     * Returns the name of this module.
     *
     * @return The module's name
     */
    String getName();

}
