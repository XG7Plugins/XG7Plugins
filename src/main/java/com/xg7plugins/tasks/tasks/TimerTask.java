package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TimerTask {

    private final String id;
    private final long delay;
    private final long period;
    private TaskState taskState;
    private final Task task;
    private final String executorName;

    public TimerTask(Plugin plugin, String id, long delay, long period, TaskState state, boolean isBukkitAsync) {
        this.id = id;
        this.delay = delay;
        this.period = period;
        this.taskState = state;
        this.executorName = null;

        this.task = isBukkitAsync ?  BukkitTask.of(plugin, true, this::run) :  BukkitTask.of(plugin, this::run);
    }
    public TimerTask(Plugin plugin, String id, long delay, long period, TaskState state, String executorName) {
        this.id = id;
        this.delay = delay;
        this.period = period;
        this.taskState = state;
        this.executorName = executorName;
        this.task = AsyncTask.of(plugin, this::run);
    }

    public TimerTask of(Plugin plugin, String id, TaskState taskState, long delay, long period, boolean isBukkit, Runnable runnable) {
        return new TimerTask(plugin, id, delay, period, taskState, isBukkit) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }
    public TimerTask of(Plugin plugin, String id, TaskState taskState, long delay, long period, String executorName, Runnable runnable) {
        return new TimerTask(plugin, id, delay, period, taskState, executorName) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    public abstract void run();

}
