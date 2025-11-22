package com.xg7plugins.extensions;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.events.Listener;
import com.xg7plugins.tasks.tasks.TimerTask;

import java.util.List;

public interface Extension {

    Plugin getPlugin();
    String getName();

    void onEnable();
    void onReload();
    void onDisable();


    default List<TimerTask> getTimerTasks() {
        return null;
    }
    default List<Listener> getListeners() {
        return null;
    }
    default List<Command> getCommands() {
        return null;
    }

    default <T extends ExtensionAPI> T getAPI() {
        return null;
    }

}
