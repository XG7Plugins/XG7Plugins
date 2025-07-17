package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * Abstract base class for tasks that can be scheduled and executed by the plugin
 */
@Setter
@Getter
public abstract class Task {

    /**
     * Reference to the plugin that owns this task
     */
    private final Plugin plugin;

    /**
     * Constructor
     *
     * @param plugin The plugin that owns this task
     */
    public Task(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Execute the task logic
     */
    public abstract void run();

    /**
     * Cancel/stop the task execution
     */
    public abstract void cancel();
}