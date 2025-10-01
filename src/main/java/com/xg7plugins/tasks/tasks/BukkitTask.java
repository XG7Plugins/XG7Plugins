package com.xg7plugins.tasks.tasks;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 * A task implementation that runs on the Bukkit scheduler.
 * Extends the base Task class with Bukkit-specific functionality.
 *
 * @see Task
 */
@Getter
@Setter
public abstract class BukkitTask implements Task {

    /**
     * The ID of the task scheduled in Bukkit
     */
    public int bukkitTaskId;

    /**
     * Flag indicating if this task runs asynchronously
     */
    private final boolean isAsync;

    /**
     * Creates a Bukkit task
     */
    public BukkitTask() {
        this.isAsync = false;
    }

    /**
     * Creates a Bukkit task with specified sync mode
     *
     * @param isAsync True to run async, false for sync
     */
    public BukkitTask(boolean isAsync) {
        super();
        this.isAsync = isAsync;
    }

    /**
     * Creates a synchronous Bukkit task from a Runnable
     *
     * @param runnable The runnable to execute
     * @return A new BukkitTask instance
     */
    public static BukkitTask of(Runnable runnable) {
        return new BukkitTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Creates a Bukkit task with specified sync mode from a Runnable
     *
     * @param isAsync  True to run async, false for sync
     * @param runnable The runnable to execute
     * @return A new BukkitTask instance
     */
    public static BukkitTask of(boolean isAsync, Runnable runnable) {
        return new BukkitTask(isAsync) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }


    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(bukkitTaskId);
    }
}