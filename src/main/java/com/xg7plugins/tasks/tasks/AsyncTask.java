package com.xg7plugins.tasks.tasks;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * Represents an asynchronous task that can be executed independently of the main server thread.
 * This class extends the base Task class and provides functionality for managing asynchronous operations.
 *
 * @see Task
 */
@Getter
@Setter
public abstract class AsyncTask implements Task {

    /**
     * The future object representing the scheduled task
     */
    private ScheduledFuture<?> taskFuture;

    /**
     * The name of the executor service that will run this task
     */
    private String executorName;

    /**
     * Creates a new AsyncTask with the specified plugin and executor name
     *
     * @param executorName The name of the executor service to use
     */
    public AsyncTask(String executorName) {
        this.executorName = executorName;
    }

    /**
     * Creates a new AsyncTask with the specified plugin, executor name and runnable
     *
     * @param executorName The name of the executor service to use
     * @param runnable     The code to execute
     * @return A new AsyncTask instance
     */
    public static AsyncTask of(String executorName, Runnable runnable) {
        return new AsyncTask(executorName) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Creates a new AsyncTask with the specified plugin and runnable, using the default executor
     *
     * @param runnable The code to execute
     * @return A new AsyncTask instance
     */
    public static AsyncTask of(Runnable runnable) {
        return new AsyncTask(null) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Cancels this task if it is currently scheduled
     */
    @Override
    public void cancel() {
        if (taskFuture == null) return;
        taskFuture.cancel(false);
    }
}