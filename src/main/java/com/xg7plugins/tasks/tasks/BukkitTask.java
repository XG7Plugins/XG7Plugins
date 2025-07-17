package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
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
public abstract class BukkitTask extends Task {

    /**
     * The ID of the task scheduled in Bukkit
     */
    public int bukkitTaskId;

    /**
     * Flag indicating if this task runs asynchronously
     */
    private final boolean isAsync;

    /**
     * Creates a synchronous Bukkit task
     *
     * @param plugin The plugin that owns this task
     */
    public BukkitTask(Plugin plugin) {
        super(plugin);
        this.isAsync = false;
    }

    /**
     * Creates a Bukkit task with specified sync mode
     *
     * @param plugin  The plugin that owns this task
     * @param isAsync True to run async, false for sync
     */
    public BukkitTask(Plugin plugin, boolean isAsync) {
        super(plugin);
        this.isAsync = isAsync;
    }

    /**
     * Creates a synchronous Bukkit task from a Runnable
     *
     * @param plugin   The plugin that owns this task
     * @param runnable The runnable to execute
     * @return A new BukkitTask instance
     */
    public static BukkitTask of(Plugin plugin, Runnable runnable) {
        return new BukkitTask(plugin) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Creates a Bukkit task with specified sync mode from a Runnable
     *
     * @param plugin   The plugin that owns this task
     * @param isAsync  True to run async, false for sync
     * @param runnable The runnable to execute
     * @return A new BukkitTask instance
     */
    public static BukkitTask of(Plugin plugin, boolean isAsync, Runnable runnable) {
        return new BukkitTask(plugin, isAsync) {
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