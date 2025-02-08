package com.xg7plugins.extensions;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.Listener;
import com.xg7plugins.tasks.Task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface Extension {

    void onInit();

    void disable();

    default Map<String, ExecutorService> getExecutors() {
        return Collections.emptyMap();
    }

    default List<Task> loadTasks() {
        return Collections.emptyList();
    }

    default List<Listener> loadListeners() {
        return Collections.emptyList();
    }

    default List<ICommand> loadCommands() {
        return Collections.emptyList();
    }

    Plugin getPlugin();
    String getName();

}
