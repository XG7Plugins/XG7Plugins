package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.TimerTask;
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
    private ScheduledExecutorService mainScheduledAsyncExecutor;

    public TaskManager() {
        load();
    }

    public void load() {
        mainScheduledAsyncExecutor = Executors.newScheduledThreadPool(Config.mainConfigOf(XG7Plugins.getInstance()).get("scheduled-tasks-threads", Integer.class).orElse(1));

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
            XG7Plugins.getInstance().getDebug().info("Registering task: " + timerTask.getId());
            if (timerTask.getTaskState() == TaskState.RUNNING) {
                timerTask.setTaskState(TaskState.IDLE);
                runTimerTask(timerTask);
                return;
            }

            String taskId = timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId();
            timerTaskMap.put(taskId, timerTask);

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

        int taskID = Bukkit.getScheduler().runTask(bukkitTask.getPlugin(), bukkitTask::run).getTaskId();

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

        int taskID = Bukkit.getScheduler().runTaskAsynchronously(bukkitTask.getPlugin(), bukkitTask::run).getTaskId();

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

        int taskID = Bukkit.getScheduler().runTaskLater(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

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

        int taskID = Bukkit.getScheduler().runTaskLaterAsynchronously(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay)).getTaskId();

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
    public void scheduleSyncRepeating(BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimer(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

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
    public void scheduleAsyncRepeatingBukkitTask(BukkitTask bukkitTask, long delay, long period) {
        if (bukkitTask == null) return;

        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(bukkitTask.getPlugin(), bukkitTask::run, TimeParser.convertMillisToTicks(delay), TimeParser.convertMillisToTicks(period)).getTaskId();

        bukkitTask.setBukkitTaskId(taskID);
    }

    /**
     * Cancels a repeating task for a plugin.
     *
     * @param plugin The plugin that owns the task
     * @param id     The task identifier
     */
    public void cancelRepeatingTask(Plugin plugin, String id) {
        TimerTask timerTask = timerTaskMap.get(plugin + ":" + id);

        if (timerTask == null) return;

        cancelRepeatingTask(timerTask);
    }

    /**
     * Cancels a repeating task by its ID.
     *
     * @param id The task identifier
     */
    public void cancelRepeatingTask(@NotNull String id) {
        TimerTask timerTask = timerTaskMap.get(id);

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
                .filter(s -> s.startsWith(plugin.getName() + ":"))
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

        timerTaskMap.remove(timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId());
    }

    /**
     * Deletes a repeating task for a plugin.
     *
     * @param plugin The plugin that owns the task
     * @param id     The task identifier
     */
    public void deleteRepeatingTask(Plugin plugin, String id) {
        TimerTask timerTask = timerTaskMap.get(plugin.getName() + ":" + id);

        if (timerTask == null) return;

        deleteRepeatingTask(timerTask);

    }

    /**
     * Deletes a repeating task by its ID.
     *
     * @param id The task identifier
     */
    public void deleteRepeatingTask(String id) {
        TimerTask timerTask = timerTaskMap.get(id);

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
                .filter(s -> s.startsWith(plugin.getName() + ":"))
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

        String taskId = timerTask.getTask().getPlugin().getName() + ":" + timerTask.getId();
        timerTaskMap.put(taskId, timerTask);

        if (timerTask.getTaskState() == TaskState.RUNNING) return;

        if (timerTask.getTask() instanceof BukkitTask) {
            BukkitTask bukkitTask = (BukkitTask) timerTask.getTask();
            if (bukkitTask.isAsync())
                scheduleAsyncRepeatingBukkitTask(bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
            else scheduleSyncRepeating(bukkitTask, timerTask.getDelay(), timerTask.getPeriod());
        } else scheduleAsyncRepeating((AsyncTask) timerTask.getTask(), timerTask.getDelay(), timerTask.getPeriod());

        timerTask.setTaskState(TaskState.RUNNING);
    }

    /**
     * Reloads all tasks for a plugin.
     *
     * @param plugin The plugin whose tasks should be reloaded
     */
    public void reloadTasks(Plugin plugin) {
        timerTaskMap.values().stream().filter(timerTask -> timerTask.getTask().getPlugin().getName().equals(plugin.getName())).forEach(this::runTimerTask);
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
     * @param id The task identifier
     * @return The task if found, null otherwise
     */
    public TimerTask getTimerTask(String id) {
        return timerTaskMap.get(id);
    }
    public TimerTask getTimerTask(Plugin plugin, String id) {
        return getTimerTask(plugin.getName() + ":" + id);
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
