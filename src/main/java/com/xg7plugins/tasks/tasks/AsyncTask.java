package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
public abstract class AsyncTask extends Task {

    private ScheduledFuture<?> taskFuture;
    private String executorName;

    /**
     * Constructs a new Task with specified parameters for repeatable tasks.
     *
     * @param plugin The plugin that owns this task
     */
    public AsyncTask(Plugin plugin, String executorName) {
        super(plugin);
        this.executorName = executorName;
    }

    public static AsyncTask of(Plugin plugin, String executorName, Runnable runnable) {
        return new AsyncTask(plugin, executorName) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    public static AsyncTask of(Plugin plugin, Runnable runnable) {
        return new AsyncTask(plugin, null) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    @Override
    public void cancel() {
        taskFuture.cancel(false);
    }
}
