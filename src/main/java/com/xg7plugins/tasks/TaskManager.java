package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class TaskManager {


    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService repeatingAsyncTasksExecutor;
    private final Map<String, ExecutorService> asyncExecutors = new HashMap<>();

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");
        repeatingAsyncTasksExecutor = Executors.newScheduledThreadPool(config.get("repeating-tasks-threads", Integer.class).orElse(1));
    }

    public void registerExecutor(String name, ExecutorService executor) {
        asyncExecutors.put(name, executor);
    }
    public void removeExecutor(String name) {
        asyncExecutors.get(name).shutdown();
        asyncExecutors.remove(name);
    }

    public void registerTasks(Task... tasks) {
        Arrays.stream(tasks).forEach(task -> {
            if (task.getState() == TaskState.RUNNING) {
                task.setState(TaskState.IDLE);
                runTask(task);
            } else this.tasks.put(task.getPlugin().getName() + ":" + task.getName(), task);

        });
    }

    public void runTask(Task task) {
        if (task == null) return;

        String taskId = task.getPlugin().getName() + ":" + task.getName();
        tasks.put(taskId, task);

        if (task.getState() == TaskState.RUNNING) return;

        Runnable taskRunnable = () -> {
            long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            try {
                task.getRunnable().run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            task.setRAMUsage(after - before);

            if (!task.isRepeating()) {
                task.setState(TaskState.STOPED);
                if (task.isAsync()) task.getFuture().cancel(true);
                else Bukkit.getScheduler().cancelTask(task.getBukkitTaskId());

                tasks.remove(taskId);
            }
        };

        if (task.isRepeating()) {
            if (task.isAsync()) {
                task.setFuture(repeatingAsyncTasksExecutor.scheduleWithFixedDelay(taskRunnable, 0, task.getDelay(), TimeUnit.MILLISECONDS));
            } else {
                task.setBukkitTaskId(
                        Bukkit.getScheduler().runTaskTimer(
                                task.getPlugin(),
                                taskRunnable,
                                0,
                                task.getDelay()
                        ).getTaskId()
                );
            }
        } else {
            if (task.isAsync()) {
                task.setFuture(repeatingAsyncTasksExecutor.schedule(taskRunnable, 0, TimeUnit.MILLISECONDS));
            } else {
                task.setBukkitTaskId(
                        Bukkit.getScheduler().runTask(
                                task.getPlugin(),
                                taskRunnable
                        ).getTaskId()
                );
            }
        }

        task.setState(TaskState.RUNNING);
    }

    public void cancelTask(String id) {
        Task task = tasks.get(id);
        if (task == null) return;

        if (task.isAsync()) {
            task.getFuture().cancel(true);
        } else {
            Bukkit.getScheduler().cancelTask(task.getBukkitTaskId());
        }
        if (!task.isRepeating()) tasks.remove(id);

        task.setState(TaskState.STOPED);
    }


    public void shutdown() {
        repeatingAsyncTasksExecutor.shutdown();
        asyncExecutors.values().forEach(ExecutorService::shutdown);
    }

}
