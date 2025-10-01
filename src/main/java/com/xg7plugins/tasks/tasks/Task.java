package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * Abstract base class for tasks that can be scheduled and executed by the plugin
 */
public interface Task {

    /**
     * Execute the task logic
     */
    void run();

    /**
     * Cancel/stop the task execution
     */
    void cancel();
}