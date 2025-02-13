package com.xg7plugins.modules;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.tasks.Task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface Module {

    void onInit();

    void onDisable();

    default Map<String, ExecutorService> getExecutors() {
        return Collections.emptyMap();
    }

    default List<Task> loadTasks() {
        return Collections.emptyList();
    }

    default List<Listener> loadListeners() {
        return Collections.emptyList();
    }

    String getName();

}
