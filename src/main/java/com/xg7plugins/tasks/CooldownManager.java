package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.tasks.tasks.CooldownManagerTask;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class CooldownManager implements Manager {

    private final ConcurrentHashMap<UUID, Map<String, CooldownTask>> cooldowns = new ConcurrentHashMap<>();
    private final Task task;
    private final List<Pair<UUID, String>> toRemove = new ArrayList<>();

    public CooldownManager(XG7Plugins plugin) {
        this.task = new CooldownManagerTask(this, Config.mainConfigOf(plugin).getTime("player-cooldown-task-delay").orElse(1000L));
    }

    public void addCooldown(Player player, CooldownTask task) {
        XG7PluginsAPI.taskManager().runTask(this.task);
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(task.getId(), task);
    }
    public void addCooldown(Player player, String cooldownId, double time) {
        XG7PluginsAPI.taskManager().runTask(task);
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
    }

    public void cancelTask() {
        XG7PluginsAPI.taskManager().cancelTask(task);
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
