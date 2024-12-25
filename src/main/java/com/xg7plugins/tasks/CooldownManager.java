package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CooldownManager {

    private final ConcurrentHashMap<UUID, Map<String, CooldownTask>> cooldowns = new ConcurrentHashMap<>();
    private final long timeFactor;
    private String taskId;

    public CooldownManager(XG7Plugins plugin) {
        this.timeFactor = plugin.getConfigsManager().getConfig("config").getTime("player-cooldown-task-delay").orElse(1000L);
    }

    public void addCooldown(Player player, CooldownTask task) {
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(task.getId(), task);
    }
    public void addCooldown(Player player, String cooldownId, double time) {
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(cooldownId, new CooldownTask(cooldownId, time, null, null));
    }

    public boolean containsPlayer(String cooldownId, Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && cooldowns.get(player.getUniqueId()).containsKey(cooldownId);
    }

    public double getReamingTime(String cooldownId, Player player) {
        return cooldowns.get(player.getUniqueId()).get(cooldownId).getTime();
    }

    public void removePlayer(String cooldownId, UUID playerID) {
        CooldownTask task = cooldowns.get(playerID).get(cooldownId);

        if (task.getOnFinish() != null) task.getOnFinish().accept(Bukkit.getPlayer(playerID), true);
        cooldowns.get(playerID).remove(cooldownId);

        if (cooldowns.get(playerID).isEmpty()) cooldowns.remove(playerID);
    }
    public void initTask() {
        XG7Plugins.taskManager().addAsyncRepeatingTask(XG7Plugins.getInstance(), "cooldown-task",() -> {
            cooldowns.forEach((id, tasks) -> {
                Player player = Bukkit.getPlayer(id);

                tasks.values().forEach(task -> {
                    task.setTime(task.getTime() - timeFactor);


                    if (player == null) {
                        removePlayer(task.getId(), id);
                        return;
                    }

                    if (task.getTick() != null) task.getTick().accept(player);
                    if (task.getTime() <= 0) {
                        if (task.getOnFinish() != null) task.getOnFinish().accept(player, false);
                        cooldowns.get(id).remove(task.getId());

                        if (cooldowns.get(id).isEmpty()) cooldowns.remove(id);
                    }
                });



            });
        },timeFactor);
    }

    public void cancelTask() {
        if (taskId == null) return;
        XG7Plugins.taskManager().cancelTask(taskId);
        taskId = null;
    }


    @Setter
    @AllArgsConstructor
    @Getter
    public static class CooldownTask {
        private String id;
        private double time;
        private final Consumer<Player> tick;
        private final BiConsumer<Player, Boolean> onFinish;
    }

}
