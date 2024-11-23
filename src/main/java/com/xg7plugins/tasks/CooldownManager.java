package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
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

    public CooldownManager(XG7Plugins plugin) {
        this.timeFactor = plugin.getConfigsManager().getConfig("config").get("player-cooldown-task-delay");
    }

    public void addCooldown(Player player, CooldownTask task) {
        cooldowns.put(player.getUniqueId(), task);
        if (taskId == null) {
            initTask();
        }
    }

    public void containsPlayer(Player player) {
        cooldowns.containsKey(player.getUniqueId());
    }
    public void removePlayer(Player player) {
        cooldowns.get(player.getUniqueId()).onFinish(player, true);
        cooldowns.remove(player.getUniqueId());
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
                    task.onFinish(Bukkit.getPlayer(id),false);
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
        public abstract void onFinish(Player player, boolean error);
    }

}
