package com.xg7plugins.extensions;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.events.Listener;
import com.xg7plugins.tasks.tasks.TimerTask;

import java.util.List;

/**
 * Extension interface for creating a plugin extension.
 * Extensions can register commands, listeners, and timer tasks, and have lifecycle methods.
 */
public interface Extension {

    /**
     * Gets the plugin associated with this extension.
     * @return The plugin instance
     */
    Plugin getPlugin();

    /**
     * Gets the name of the extension.
     * @return The extension name
     */
    String getName();

    /**
     * On enable logic
     */
    void onEnable();

    /**
     * On reload logic
     */
    void onReload();

    /**
     * On disable logic
     */
    void onDisable();

    /**
     * Gets the version of the extension.
     * @return The extension version
     */
    default List<TimerTask> getTimerTasks() {
        return null;
    }

    /**
     * Gets the listeners registered by this extension.
     * @return List of listeners
     */
    default List<Listener> getListeners() {
        return null;
    }

    /**
     * Gets the commands registered by this extension.
     * @return List of commands
     */
    default List<Command> getCommands() {
        return null;
    }

    /**
     * Gets the API provided by this extension.
     *
     * @param <T> The type of the extension API
     * @return The extension API instance
     */
    default <T extends ExtensionAPI> T getAPI() {
        return null;
    }

}
