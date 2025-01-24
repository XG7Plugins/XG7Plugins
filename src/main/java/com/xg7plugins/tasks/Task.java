package com.xg7plugins.tasks;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

@Setter
@Getter
public class Task {

    private final long delay;
    private final Plugin plugin;
    private final String name;
    private final boolean isAsync;
    private final boolean isRepeating;
    private TaskState state;
    private ScheduledFuture<?> future;
    private int bukkitTaskId;
    private final Runnable runnable;
    private final String executorName;

    public Task(Plugin plugin, String name, boolean isAsync, boolean isRepeating, long delay, TaskState state, Runnable runnable) {
        this.plugin = plugin;
        this.name = name;
        this.isAsync = isAsync;
        this.isRepeating = isRepeating;
        this.delay = delay;
        this.runnable = runnable;
        this.state = state;
        this.executorName = null;
    }
    public Task(Plugin plugin, String name, boolean isAsync, String executorName, long delay, TaskState state, Runnable runnable) {
        this.plugin = plugin;
        this.name = name;
        this.isAsync = isAsync;
        this.isRepeating = false;
        this.delay = delay;
        this.runnable = runnable;
        this.state = state;
        this.executorName = executorName;
    }

    public void run() {
        runnable.run();
    }
}
