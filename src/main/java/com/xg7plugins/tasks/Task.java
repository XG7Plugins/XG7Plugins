package com.xg7plugins.tasks;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * Represents a scheduled task in the plugin system.
 * This class encapsulates all the information needed to execute and manage a task,
 * including its timing, state, and execution details.
 */
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

    /**
     * Constructs a new Task with specified parameters for repeatable tasks.
     *
     * @param plugin      The plugin that owns this task
     * @param name        The name of the task
     * @param isAsync     Whether the task should run asynchronously
     * @param isRepeating Whether the task should repeat
     * @param delay       The delay before/between executions in ticks
     * @param state       The initial state of the task
     * @param runnable    The code to be executed
     */
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

    /**
     * Constructs a new Task with specified parameters for single-execution tasks with an executor.
     *
     * @param plugin       The plugin that owns this task
     * @param name         The name of the task
     * @param isAsync      Whether the task should run asynchronously
     * @param executorName The name of the executor for this task
     * @param delay        The delay before execution in ticks
     * @param state        The initial state of the task
     * @param runnable     The code to be executed
     */
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

    /**
     * Executes the task's runnable code.
     */
    public void run() {
        runnable.run();
    }
}
