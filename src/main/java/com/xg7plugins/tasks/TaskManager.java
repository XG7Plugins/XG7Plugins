package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.Task;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.utils.time.TimeParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages and executes both synchronous and asynchronous tasks for plugins.
 * Provides functionality for scheduling, running, canceling and managing tasks
 * with support for different executor services.
 */
@Getter
public class TaskManager implements Manager {


    private final Map<String, TimerTask> timerTaskMap = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> asyncExecutors = new HashMap<>();
    private final ScheduledExecutorService mainScheduledAsyncExecutor;

    /**
     * Initializes the TaskManager with necessary thread pools and executors.
     *
     * @param plugin The main plugin instance
     */
    public TaskManager(XG7Plugins plugin) {
        Config config = Config.mainConfigOf(plugin);
        mainScheduledAsyncExecutor = Executors.newScheduledThreadPool(config.get("repeating-tasks-threads", Integer.class).orElse(1));

        registerExecutor("commands", Executors.newSingleThreadExecutor());
        registerExecutor("database", Executors.newCachedThreadPool());
        registerExecutor("files", Executors.newCachedThreadPool());
        registerExecutor("langs", Executors.newCachedThreadPool());
        registerExecutor("menus", Executors.newSingleThreadExecutor());
        registerExecutor("cache", Executors.newSingleThreadExecutor());
    }

    /**
     * Registers a new executor service with a specified name.
     *
     * @param name     The name to identify the executor
     * @param executor The ExecutorService instance to register
     */
    public void registerExecutor(String name, ExecutorService executor) {
        asyncExecutors.put(name, executor);
    }

    public void removeExecutor(String name) {
        asyncExecutors.get(name).shutdown();
        asyncExecutors.remove(name);
    }

    /**
     * Registers multiple tasks at once using varargs.
     *
     * @param tasks Array of tasks to register
     */
    public void registerTimerTasks(TimerTask... tasks) {
        registerTimerTasks(Arrays.asList(tasks));
    }
    public void registerTimerTasks(List<TimerTask> tasks) {
        if (tasks == null) return;
        tasks.forEach(timerTask -> {
            if (timerTask == null) return;
            if (timerTask.getTaskState() == TaskState.RUNNING) {
                timerTask.setTaskState(TaskState.IDLE);
                runTimerTask(timerTask);
                return;
            }

            String taskId = timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId();
            timerTaskMap.put(taskId, timerTask);

        });
    }

    public TimerTask getRegisteredTimerTask(Plugin plugin, String id) {
        return timerTaskMap.get(plugin.getName() + ":" + id);
    }

    @SneakyThrows
    public void runSync(BukkitTask bukkitTask) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTask(bukkitTask.getPlugin(), bukkitTask::run).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }
    public void runAsync(AsyncTask asyncTask) {
        if (asyncTask == null) return;

        ExecutorService taskExecutor = asyncTask.getExecutorName() == null ? mainScheduledAsyncExecutor : asyncExecutors.get(asyncTask.getExecutorName());

        taskExecutor.submit(() -> {
            try {
                asyncTask.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @SneakyThrows
    public void runAsyncBukkitTask(BukkitTask bukkitTask) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskAsynchronously(bukkitTask.getPlugin(), bukkitTask::run).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    @SneakyThrows
    public void scheduleSync(BukkitTask bukkitTask, long delay) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskLater(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }
    public void scheduleAsync(AsyncTask asyncTask, long delay) {
        if (asyncTask == null) return;

        ScheduledExecutorService taskExecutor = asyncTask.getExecutorName() == null ? mainScheduledAsyncExecutor : (ScheduledExecutorService) asyncExecutors.get(asyncTask.getExecutorName());

        asyncTask.setTaskFuture(taskExecutor.schedule(() -> {
            try {
                asyncTask.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, delay, TimeUnit.MILLISECONDS));
    }

    @SneakyThrows
    public void scheduleAsyncBukkitTask(BukkitTask bukkitTask, long delay) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskLaterAsynchronously(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    @SneakyThrows
    public void scheduleSyncRepeating(BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimer(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }
    public void scheduleAsyncRepeating(AsyncTask asyncTask, long delay, long period) {
        if (asyncTask == null) return;

        ScheduledExecutorService taskExecutor = asyncTask.getExecutorName() == null ? mainScheduledAsyncExecutor : (ScheduledExecutorService) asyncExecutors.get(asyncTask.getExecutorName());

        asyncTask.setTaskFuture(taskExecutor.scheduleWithFixedDelay(() -> {
            try {
                asyncTask.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, delay, period, TimeUnit.MILLISECONDS));
    }
    @SneakyThrows
    public void scheduleAsyncRepeatingBukkitTask(BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    public void cancelRepeatingTask(Plugin plugin, String id) {
        TimerTask timerTask = timerTaskMap.get(plugin + ":" + id);

        if (timerTask == null) return;

        cancelRepeatingTask(timerTask);
    }

    public void cancelRepeatingTask(@NotNull String id) {
        TimerTask timerTask = timerTaskMap.get(id);

        if (timerTask == null) return;

        cancelRepeatingTask(timerTask);
    }

    public void cancelRepeatingTask(@NotNull TimerTask timerTask) {
        timerTask.getTask().cancel();

        timerTask.setTaskState(TaskState.IDLE);
    }

    public void cancelAllRegisteredTasks(Plugin plugin) {
        timerTaskMap.keySet().stream()
                .filter(s -> s.startsWith(plugin.getName() + ":"))
                .map(timerTaskMap::get)
                .forEach(this::cancelRepeatingTask);
    }

    public void deleteRepeatingTask(TimerTask timerTask) {
        cancelRepeatingTask(timerTask);

        timerTaskMap.remove(timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId());
    }

    public void deleteRepeatingTask(Plugin plugin, String id) {
        TimerTask timerTask = timerTaskMap.get(plugin.getName() + ":" + id);

        if (timerTask == null) return;

        deleteRepeatingTask(timerTask);

    }

    public void deleteRepeatingTask(String id) {
        TimerTask timerTask = timerTaskMap.get(id);

        if (timerTask == null) return;

        deleteRepeatingTask(timerTask);

    }

    public void deleteAllRepeatingTasks(Plugin plugin) {
        timerTaskMap.keySet().stream()
                .filter(s -> s.startsWith(plugin.getName() + ":"))
                .map(timerTaskMap::get)
                .forEach(this::deleteRepeatingTask);
    }

    public void runTimerTask(TimerTask timerTask) {
        if (timerTask == null) return;

        String taskId = timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId();
        timerTaskMap.put(taskId, timerTask);

        if (timerTask.getTaskState() == TaskState.RUNNING) return;

        if (timerTask.getTask() instanceof BukkitTask) {
            BukkitTask bukkitTask = (BukkitTask) timerTask.getTask();
            if (bukkitTask.isAsync()) scheduleAsyncRepeatingBukkitTask(bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
            else scheduleSyncRepeating(bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
        }
        else scheduleAsyncRepeating((AsyncTask) timerTask.getTask(), timerTask.getDelay(), timerTask.getPeriod());

        timerTask.setTaskState(TaskState.RUNNING);
    }

    /**
     * Shuts down all executor services and clears task collections.
     * Should be called when the plugin is being disabled.
     */
    public void shutdown() {
        mainScheduledAsyncExecutor.shutdown();
        asyncExecutors.values().forEach(ExecutorService::shutdown);
        this.timerTaskMap.clear();
        this.asyncExecutors.clear();
    }

    public void reloadTasks(Plugin plugin) {
        timerTaskMap.values().stream().filter(timerTask -> timerTask.getTask().getPlugin().getName().equals(plugin.getName())).forEach(this::runTimerTask);
    }
    public ExecutorService getExecutor(String name) {
        return asyncExecutors.get(name);
    }

    /**
     * Retrieves a task by its identifier.
     *
     * @param id The task identifier
     * @return The task if found, null otherwise
     */
    public TimerTask getTimerTask(String id) {
        return timerTaskMap.get(id);
    }

    /**
     * Checks if a task with the given ID exists.
     *
     * @param id The task identifier to check
     * @return true if the task exists, false otherwise
     */
    public boolean containsTimerTask(String id) {
        return timerTaskMap.containsKey(id);
    }

}
