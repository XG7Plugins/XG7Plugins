package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.Time;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class TaskManager implements Manager {


    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService repeatingAsyncTasksExecutor;
    private final Map<String, ExecutorService> asyncExecutors = new HashMap<>();

    public TaskManager(XG7Plugins plugin) {
        plugin.getDebug().loading("Loading task manager...");
        Config config = plugin.getConfigsManager().getConfig("config");
        repeatingAsyncTasksExecutor = Executors.newScheduledThreadPool(config.get("repeating-tasks-threads", Integer.class).orElse(1));

        registerExecutor("commands", Executors.newCachedThreadPool());
        registerExecutor("database", Executors.newCachedThreadPool());
        registerExecutor("files", Executors.newCachedThreadPool());
        registerExecutor("menus", Executors.newCachedThreadPool());
        registerExecutor("cache", Executors.newSingleThreadExecutor());
    }

    public void registerExecutor(String name, ExecutorService executor) {
        asyncExecutors.put(name, executor);
    }
    public void removeExecutor(String name) {
        asyncExecutors.get(name).shutdown();
        asyncExecutors.remove(name);
    }

    public void registerTasks(Task... tasks) {
        if (tasks == null) return;
        Arrays.stream(tasks).forEach(task -> {
            if (task == null) return;
            if (task.getState() == TaskState.RUNNING) {
                task.setState(TaskState.IDLE);
                runTask(task);
            } else this.tasks.put(task.getPlugin().getName() + ":" + task.getName(), task);

        });
    }
    public void registerTasks(List<Task> tasks) {
        if (tasks == null) return;
        tasks.forEach(task -> {
            if (task == null) return;
            if (task.getState() == TaskState.RUNNING) {
                task.setState(TaskState.IDLE);
                runTask(task);
            } else this.tasks.put(task.getPlugin().getName() + ":" + task.getName(), task);
        });
    }

    public Task getRegisteredTask(Plugin plugin, String id) {
        return tasks.get(plugin.getName() + ":" + id);
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
                task.run();
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
                                Time.convertMillisToTicks(task.getDelay())
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
        this.tasks.clear();
        this.asyncExecutors.clear();
    }

}
