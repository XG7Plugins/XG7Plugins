package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class TaskManager {


    private final Map<String, ScheduledFuture<?>> tasksRunning = new HashMap<>();
    private final ScheduledExecutorService executor;

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");
        executor = Executors.newScheduledThreadPool(config.get("task-threads"));
    }

    public String addRepeatingTask(Plugin plugin, String name, Runnable runnable, long delay) {
        String taskId = plugin + ":" + name + ":" + UUID.randomUUID();
        tasksRunning.put(taskId, executor.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MILLISECONDS));
        return taskId;
    }

    public void runTask(Runnable runnable) {
        CompletableFuture.runAsync(runnable,executor);

    }
    public void runTaskSync(Plugin pl, Runnable runnable) {
        Bukkit.getScheduler().runTask(pl,runnable);
    }

    public void cancelTask(String id) {
        tasksRunning.get(id).cancel(false);
        tasksRunning.remove(id);
    }

    public void cancelTask(UUID id) {

        for (String taskId : tasksRunning.keySet()) {
            if (taskId.endsWith(id.toString())) {
                tasksRunning.get(taskId).cancel(false);
                tasksRunning.remove(taskId);
                return;
            }
        }
    }

    public boolean exists(String id) {
        return tasksRunning.containsKey(id);
    }
    public boolean exists(UUID id) {
        return tasksRunning.keySet().stream().anyMatch(taskId -> taskId.endsWith(id.toString()));
    }





}
