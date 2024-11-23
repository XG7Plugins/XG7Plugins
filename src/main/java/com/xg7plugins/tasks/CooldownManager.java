package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final ConcurrentHashMap<UUID, CooldownTask> cooldowns = new ConcurrentHashMap<>();
    private final int timeFactor;
    private String taskId;

    public CooldownManager(int timeFactor) {
        this.timeFactor = timeFactor;
    }

    public void addCooldown(UUID player, CooldownTask task) {
        cooldowns.put(player, task);
        if (taskId == null) {
            initTask();
        }
    }

    private void initTask() {
        this.taskId = XG7Plugins.getInstance().getTaskManager().addRepeatingTask(XG7Plugins.getInstance(), "cooldown-task",() -> {
            if (cooldowns.isEmpty()) {
                XG7Plugins.getInstance().getTaskManager().cancelTask(taskId);
                taskId = null;
                return;
            }
            cooldowns.forEach((id, task) -> {
                task.setTime(task.getTime() - timeFactor);
                task.tick(Bukkit.getPlayer(id));
                if (task.getTime() <= 0) {
                    task.onFinish(Bukkit.getPlayer(id));
                    cooldowns.remove(id);
                }
            });
        },timeFactor);
    }


    @AllArgsConstructor
    @Getter
    public abstract static class CooldownTask {
        @Setter
        private double time;
        private TimeUnit timeUnit;
        public abstract void tick(Player player);
        public abstract void onFinish(Player player);
    }

}
