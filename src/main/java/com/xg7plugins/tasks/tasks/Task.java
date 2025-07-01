package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

@Setter
@Getter
public abstract class Task {

    private final Plugin plugin;

    /**
     * Constructs a new Task with specified parameters for repeatable tasks.
     *
     * @param plugin      The plugin that owns this task
     */
    public Task(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the task's runnable code.
     */
    public abstract void run();

    public abstract void cancel();
}
