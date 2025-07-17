package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class that represents a timer task that can be executed repeatedly
 * with a specified delay and period.
 */
@Getter
@Setter
public abstract class TimerTask {

    /**
     * Unique identifier for this task
     */
    private final String id;
    /**
     * Initial delay before first execution
     */
    private final long delay;
    /**
     * Period between executions
     */
    private final long period;
    /**
     * Current state of the task (IDLE/RUNNING)
     */
    private TaskState taskState;
    /**
     * The underlying task implementation
     */
    private final Task task;
    /**
     * Name of executor service (for async tasks)
     */
    private final String executorName;

    /**
     * Constructor for Bukkit tasks (sync/async)
     *
     * @param plugin        The plugin instance
     * @param id            Task identifier
     * @param delay         Initial delay in ticks
     * @param period        Period between executions
     * @param state         Initial task state
     * @param isBukkitAsync Whether to run async on Bukkit scheduler
     */
    public TimerTask(Plugin plugin, String id, long delay, long period, TaskState state, boolean isBukkitAsync) {
        this.id = id;
        this.delay = delay;
        this.period = period;
        this.taskState = state;
        this.executorName = null;

        this.task = isBukkitAsync ? BukkitTask.of(plugin, true, this::run) : BukkitTask.of(plugin, this::run);
    }

    /**
     * Constructor for custom executor service tasks
     *
     * @param plugin       The plugin instance
     * @param id           Task identifier
     * @param delay        Initial delay in ticks
     * @param period       Period between executions
     * @param state        Initial task state
     * @param executorName Name of executor service
     */
    public TimerTask(Plugin plugin, String id, long delay, long period, TaskState state, String executorName) {
        this.id = id;
        this.delay = delay;
        this.period = period;
        this.taskState = state;
        this.executorName = executorName;
        this.task = AsyncTask.of(plugin, this::run);
    }

    /**
     * Factory method for creating Bukkit tasks
     */
    public TimerTask of(Plugin plugin, String id, TaskState taskState, long delay, long period, boolean isBukkit, Runnable runnable) {
        return new TimerTask(plugin, id, delay, period, taskState, isBukkit) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Factory method for creating executor service tasks
     */
    public TimerTask of(Plugin plugin, String id, TaskState taskState, long delay, long period, String executorName, Runnable runnable) {
        return new TimerTask(plugin, id, delay, period, taskState, executorName) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Abstract method that defines the task's execution logic
     */
    public abstract void run();

}