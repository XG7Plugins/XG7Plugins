package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;

import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.PluginKey;
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
public class TaskManager {


    private final Map<PluginKey, TimerTask> timerTaskMap = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> asyncExecutors = new HashMap<>();
    private ScheduledExecutorService mainScheduledAsyncExecutor;

    public TaskManager() {
        load();
    }

    /**
     * Initializes the TaskManager by setting up the main scheduled async executor
     * and registering default executor services.
     */
    public void load() {
        mainScheduledAsyncExecutor = Executors.newScheduledThreadPool(ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("scheduled-tasks-threads", 1));

        registerExecutor("commands", Executors.newSingleThreadExecutor());
        registerExecutor("database", Executors.newCachedThreadPool());
        registerExecutor("files", Executors.newCachedThreadPool());
        registerExecutor("langs", Executors.newCachedThreadPool());
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

    /**
     * Registers a list of timer tasks for execution.
     *
     * @param tasks The list of timer tasks to register
     */
    public void registerTimerTasks(List<TimerTask> tasks) {
        if (tasks == null) return;
        tasks.forEach(timerTask -> {
            if (timerTask == null) return;
            XG7Plugins.getInstance().getDebug().info("tasks", "Registering task: " + timerTask.getId());
            if (timerTask.getTaskState() == TaskState.RUNNING) {
                timerTask.setTaskState(TaskState.IDLE);
                runTimerTask(timerTask);
                return;
            }

            timerTaskMap.put(PluginKey.of(timerTask.getPlugin(), timerTask.getId()), timerTask);
        });

    }
    /**
     * Runs a task synchronously on the main server thread.
     *
     * @param bukkitTask The Bukkit task to run
     */
    @SneakyThrows
    public void runSync(BukkitTask bukkitTask) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTask(XG7Plugins.getInstance().getJavaPlugin(), bukkitTask::run).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Runs a task asynchronously using the executor service.
     *
     * @param asyncTask The async task to run
     */
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

    /**
     * Runs a Bukkit task asynchronously.
     *
     * @param bukkitTask The Bukkit task to run asynchronously
     */
    @SneakyThrows
    public void runAsyncBukkitTask(BukkitTask bukkitTask) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskAsynchronously(XG7Plugins.getInstance().getJavaPlugin(), bukkitTask::run).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Schedules a task to run synchronously after a delay.
     *
     * @param bukkitTask The Bukkit task to schedule
     * @param delay      The delay in milliseconds
     */
    @SneakyThrows
    public void scheduleSync(BukkitTask bukkitTask, long delay) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance().getJavaPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Schedules an async task to run after a delay.
     *
     * @param asyncTask The async task to schedule
     * @param delay     The delay in milliseconds
     */
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

    /**
     * Schedules a Bukkit task to run asynchronously after a delay.
     *
     * @param bukkitTask The Bukkit task to schedule
     * @param delay      The delay in milliseconds
     */
    @SneakyThrows
    public void scheduleAsyncBukkitTask(BukkitTask bukkitTask, long delay) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskLaterAsynchronously(XG7Plugins.getInstance().getJavaPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Schedules a task to run repeatedly on the main thread.
     *
     * @param bukkitTask The Bukkit task to schedule
     * @param delay      Initial delay in milliseconds
     * @param period     Period between executions in milliseconds
     */
    @SneakyThrows
    public void scheduleSyncRepeating(Plugin plugin, BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimer(plugin.getJavaPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Schedules an async task to run repeatedly.
     *
     * @param asyncTask The async task to schedule
     * @param delay     Initial delay in milliseconds
     * @param period    Period between executions in milliseconds
     */
    public void scheduleAsyncRepeating(AsyncTask asyncTask, long delay, long period) {
        if (asyncTask == null) return;

        ScheduledExecutorService taskExecutor = asyncTask.getExecutorName() == null ? mainScheduledAsyncExecutor : (ScheduledExecutorService) asyncExecutors.get(asyncTask.getExecutorName());

        asyncTask.setTaskFuture(taskExecutor.scheduleWithFixedDelay(() -> {
            try {
                asyncTask.run();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, delay, period, TimeUnit.MILLISECONDS));
    }

    /**
     * Schedules a Bukkit task to run repeatedly in async.
     *
     * @param bukkitTask The Bukkit task to schedule
     * @param delay      Initial delay in milliseconds
     * @param period     Period between executions in milliseconds
     */
    @SneakyThrows
    public void scheduleAsyncRepeatingBukkitTask(Plugin plugin, BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin.getJavaPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Cancels a repeating task for a plugin.
     *
     * @param taskId The task identifier
     */
    public void cancelRepeatingTask(PluginKey taskId) {
        TimerTask timerTask = timerTaskMap.get(taskId);

        if (timerTask == null) return;

        cancelRepeatingTask(timerTask);
    }

    /**
     * Cancels a specific timer task.
     *
     * @param timerTask The timer task to cancel
     */
    public void cancelRepeatingTask(@NotNull TimerTask timerTask) {
        timerTask.getTask().cancel();

        timerTask.setTaskState(TaskState.IDLE);
    }

    /**
     * Cancels all registered tasks for a plugin.
     *
     * @param plugin The plugin whose tasks should be canceled
     */
    public void cancelAllRegisteredTasks(Plugin plugin) {
        timerTaskMap.keySet().stream()
                .filter(k -> k.isSamePlugin(plugin))
                .map(timerTaskMap::get)
                .forEach(this::cancelRepeatingTask);
    }

    /**
     * Deletes a repeating timer task.
     *
     * @param timerTask The timer task to delete
     */
    public void deleteRepeatingTask(TimerTask timerTask) {
        cancelRepeatingTask(timerTask);

        timerTaskMap.remove(PluginKey.of(timerTask.getPlugin(), timerTask.getId()));
    }

    /**
     * Deletes a repeating task for a plugin.
     *
     * @param key The task identifier
     */
    public void deleteRepeatingTask(PluginKey key) {
        TimerTask timerTask = timerTaskMap.get(key);

        if (timerTask == null) return;

        deleteRepeatingTask(timerTask);

    }

    /**
     * Deletes all repeating tasks for a plugin.
     *
     * @param plugin The plugin whose tasks should be deleted
     */
    public void deleteAllRepeatingTasks(Plugin plugin) {
        timerTaskMap.keySet().stream()
                .filter(k -> k.isSamePlugin(plugin))
                .map(timerTaskMap::get)
                .forEach(this::deleteRepeatingTask);
    }

    /**
     * Runs a timer task with the specified configuration.
     *
     * @param timerTask The timer task to run
     */
    public void runTimerTask(TimerTask timerTask) {
        if (timerTask == null) return;

        timerTaskMap.put(PluginKey.of(timerTask.getPlugin(), timerTask.getId()), timerTask);

        if (timerTask.getTaskState() == TaskState.RUNNING) return;

        if (timerTask.getTask() instanceof BukkitTask) {
            BukkitTask bukkitTask = (BukkitTask) timerTask.getTask();
            if (bukkitTask.isAsync())
                scheduleAsyncRepeatingBukkitTask(timerTask.getPlugin(), bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
            else scheduleSyncRepeating(timerTask.getPlugin(), bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
        } else scheduleAsyncRepeating((AsyncTask) timerTask.getTask(), timerTask.getDelay(), timerTask.getPeriod());

        timerTask.setTaskState(TaskState.RUNNING);
    }

    /**
     * Reloads all tasks for a plugin.
     *
     * @param plugin The plugin whose tasks should be reloaded
     */
    public void reloadTasks(Plugin plugin) {
        timerTaskMap.values().stream().filter(timerTask -> timerTask.getPlugin().getName().equals(plugin.getName())).forEach(this::runTimerTask);
    }

    /**
     * Gets an executor service by name.
     *
     * @param name The name of the executor
     * @return The executor service if found
     */
    public ExecutorService getExecutor(String name) {
        return asyncExecutors.get(name);
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

    /**
     * Retrieves a task by its identifier.
     *
     * @param key The task identifier
     * @return The TimerTask if found, null otherwise
     */
    public TimerTask getTimerTask(PluginKey key) {
        return timerTaskMap.get(key);
    }

    /**
     * Checks if a task with the given ID exists.
     *
     * @param key The task identifier
     * @return True if the task exists, false otherwise
     */
    public boolean containsTimerTask(PluginKey key) {
        return timerTaskMap.containsKey(key);
    }

}
