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

/**
 * Manages cooldown timers for players.
 * Handles creation, tracking and removal of cooldown periods for various player actions.
 */
@Getter
public class CooldownManager implements Manager {

    private final ConcurrentHashMap<UUID, Map<String, CooldownTask>> cooldowns = new ConcurrentHashMap<>();
    private final Task task;
    private final List<Pair<UUID, String>> toRemove = new ArrayList<>();

    public CooldownManager(XG7Plugins plugin) {
        this.task = new CooldownManagerTask(this, Config.mainConfigOf(plugin).getTime("player-cooldown-task-delay").orElse(1000L));
    }

    /**
     * Adds a new cooldown task for a player
     *
     * @param player The player to apply the cooldown to
     * @param task   The cooldown task to add
     */
    public void addCooldown(Player player, CooldownTask task) {
        XG7PluginsAPI.taskManager().runTask(this.task);
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(task.getId(), task);
    }

    /**
     * Adds a simple cooldown for a player with specified duration
     *
     * @param player     The player to apply the cooldown to
     * @param cooldownId The unique identifier for this cooldown
     * @param time       The duration of the cooldown in seconds
     */
    public void addCooldown(Player player, String cooldownId, double time) {
        XG7PluginsAPI.taskManager().runTask(task);
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(cooldownId, new CooldownTask(cooldownId, time, null, null));
    }

    /**
     * Checks if a player has an active cooldown
     *
     * @param cooldownId The cooldown identifier to check
     * @param player     The player to check
     * @return true if player has an active cooldown, false otherwise
     */
    public boolean containsPlayer(String cooldownId, Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && cooldowns.get(player.getUniqueId()).containsKey(cooldownId);
    }

    /**
     * Gets the remaining time on a player's cooldown
     *
     * @param cooldownId The cooldown identifier to check
     * @param player     The player to check
     * @return The remaining time in seconds
     */
    public double getReamingTime(String cooldownId, Player player) {
        return cooldowns.get(player.getUniqueId()).get(cooldownId).getTime();
    }

    /**
     * Removes a cooldown from a player and executes finish callback if present
     *
     * @param cooldownId The cooldown identifier to remove
     * @param playerID   The UUID of the player
     */
    public void removePlayer(String cooldownId, UUID playerID) {
        CooldownTask task = cooldowns.get(playerID).get(cooldownId);

        if (task.getOnFinish() != null) task.getOnFinish().accept(Bukkit.getPlayer(playerID), true);
        cooldowns.get(playerID).remove(cooldownId);
    }

    /**
     * Cancels the cooldown manager task
     */
    public void cancelTask() {
        XG7PluginsAPI.taskManager().cancelTask(task);
    }


    /**
     * Represents a cooldown task with its associated data and callbacks
     */
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
