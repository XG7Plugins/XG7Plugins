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

    public void runAsyncTask(Plugin plugin, String executorName, Runnable task) {
        runTask(new Task(plugin, "task-" + UUID.randomUUID(), true, executorName, 0, TaskState.IDLE, task));
    }
    public void runSyncTask(Plugin plugin, Runnable task) {
        runTask(new Task(plugin, "task-" + UUID.randomUUID(), false, false, 0, TaskState.IDLE, task));
    }


    public void runTask(Task task) {
        if (task == null) return;

        String taskId = task.getPlugin().getName() + ":" + task.getName();
        tasks.put(taskId, task);

        if (task.getState() == TaskState.RUNNING) return;

        Runnable taskRunnable = () -> {

            try {
                task.getRunnable().run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!task.isRepeating()) {
                task.setState(TaskState.IDLE);
                cancelTask(task);
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
                asyncExecutors.get(task.getExecutorName()).submit(taskRunnable);
            } else {
                Bukkit.getScheduler().runTask(task.getPlugin(), taskRunnable);
            }
        }

        task.setState(TaskState.RUNNING);
    }

    public void cancelTask(Plugin plugin, String id) {
        Task task = tasks.get(plugin.getName() + ":" + id);
        if (task == null) return;

        cancelTask(task);
    }
    public void cancelTasks(Plugin plugin) {
        tasks.keySet().stream()
                .filter(s -> s.startsWith(plugin.getName() + ":"))
                .map(tasks::get)
                .forEach(this::cancelTask);
    }
    public void cancelTask(String id) {
        Task task = tasks.get(id);
        if (task == null) return;

        cancelTask(task);
    }

    public void cancelTask(Task task) {
        if (task.getState().equals(TaskState.RUNNING)) {
            if (task.isAsync()) {
                task.getFuture().cancel(true);
            } else {
                Bukkit.getScheduler().cancelTask(task.getBukkitTaskId());
            }
        }
        if (!task.isRepeating()) tasks.remove(task.getPlugin().getName() + ":" + task.getName());

        task.setState(TaskState.IDLE);
    }

    public void deleteTask(Task task) {
        cancelTask(task);
        tasks.remove(task.getPlugin().getName() + ":" + task.getName());
    }
    public void deleteTask(Plugin plugin, String id) {
        Task task = tasks.get(plugin.getName() + ":" + id);
        if (task == null) return;

        deleteTask(task);
    }
    public void deleteTask(String id) {
        Task task = tasks.get(id);
        if (task == null) return;

        deleteTask(task);
    }


    public void shutdown() {
        repeatingAsyncTasksExecutor.shutdown();
        asyncExecutors.values().forEach(ExecutorService::shutdown);
    }

}
