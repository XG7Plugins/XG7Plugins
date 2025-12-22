package com.xg7plugins.cooldowns;

import com.xg7plugins.config.file.ConfigFile;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Debug;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Manages cooldown timers for players.
 * Handles creation, tracking and removal of cooldown periods for various player actions.
 */
@Getter
public class CooldownManager {

    private final ConcurrentHashMap<UUID, Map<String, CooldownTask>> cooldowns = new ConcurrentHashMap<>();
    private final CooldownManagerTask task;

    public CooldownManager(XG7Plugins plugin) {
        this.task = new CooldownManagerTask(this, ConfigFile.mainConfigOf(plugin).root().getTimeInMilliseconds("player-cooldown-task-delay", 1000L));
    }

    /**
     * Adds a new cooldown task for a player
     *
     * @param player The player to apply the cooldown to
     * @param task   The cooldown task to add
     */
    public void addCooldown(Player player, CooldownTask task) {

        Debug.of(XG7Plugins.getInstance()).info("cooldown", "Adding " + player.getName() + " to cooldown " + task.getId() + " with time " + task.getTime() + "ms.");

        XG7Plugins.getAPI().taskManager().runTimerTask(this.task);
        cooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        cooldowns.get(player.getUniqueId()).put(task.getId(), task);
    }

    /**
     * Adds a simple cooldown for a player with specified duration
     *
     * @param player     The player to apply the cooldown to
     * @param cooldownId The unique identifier for this cooldown
     * @param time       The duration of the cooldown in milliseconds
     */
    public void addCooldown(Player player, String cooldownId, long time) {
        addCooldown(player, new CooldownTask(cooldownId, time, null, null));
    }

    /**
     * Checks if a player has an active cooldown
     *
     * @param cooldownId The cooldown identifier to check
     * @param player     The player to check
     * @return true if a player has an active cooldown, false otherwise
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
    public long getReamingTime(String cooldownId, Player player) {
        return cooldowns.get(player.getUniqueId()).get(cooldownId).getTime();
    }

    /**
     * Removes a cooldown from a player and executes finish callback if present
     *
     * @param cooldownId The cooldown identifier to remove
     * @param playerID   The UUID of the player
     * @param error   If the process returned an error
     */
    public void removeCooldown(String cooldownId, UUID playerID, boolean error) {

        Debug.of(XG7Plugins.getInstance()).info("cooldown", "Removing " + playerID + " from cooldown " + cooldownId + ". Errors? " + error);

        CooldownTask task = cooldowns.get(playerID).get(cooldownId);

        if (task.getOnFinish() != null) task.getOnFinish().accept(Bukkit.getPlayer(playerID), error);
        cooldowns.get(playerID).remove(cooldownId);
    }

    public void removeAll() {
        cooldowns.forEach((playerId, taskMap) -> {
            if (taskMap == null) return;

            taskMap.forEach((cooldownId, task) -> {

                if (task.getOnFinish() != null) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) task.getOnFinish().accept(player, false);

                }
            });
        });

        cooldowns.clear();
    }


    /**
     * Cancels the cooldown manager task
     */
    public void cancelTask() {
        XG7Plugins.getAPI().taskManager().cancelRepeatingTask(task);
    }


    /**
     * Represents a cooldown task with its associated data and callbacks
     */
    @Setter
    @AllArgsConstructor
    @Getter
    public static class CooldownTask {
        private String id;
        private long time;
        private final Consumer<Player> tick;
        private final BiConsumer<Player, Boolean> onFinish;
    }

}
