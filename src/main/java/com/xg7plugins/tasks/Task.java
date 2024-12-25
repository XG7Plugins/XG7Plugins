package com.xg7plugins.tasks;

import com.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

@Setter
@Getter
public class Task {

    private final long delay;
    private long RAMUsage;
    private final Plugin plugin;
    private final String name;
    private final boolean isAsync;
    private final boolean isRepeating;
    private TaskState state;
    private ScheduledFuture<?> future;
    private int bukkitTaskId;
    private final Runnable runnable;

    public Task(Plugin plugin, String name, boolean isAsync, boolean isRepeating, long delay, TaskState state, Runnable runnable) {
        this.plugin = plugin;
        this.name = name;
        this.isAsync = isAsync;
        this.isRepeating = isRepeating;
        this.delay = delay;
        this.runnable = runnable;
        this.state = state;
    }
}
