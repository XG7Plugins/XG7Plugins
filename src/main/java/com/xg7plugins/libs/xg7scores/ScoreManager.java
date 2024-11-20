package com.xg7plugins.libs.xg7scores;


import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class ScoreManager {

    private final XG7Plugins plugin;

    private final HashMap<String, Score> scoreboards = new HashMap<>();
    private final List<UUID> sendActionBlackList = new ArrayList<>();

    private String taskId;

    public ScoreManager(XG7Plugins plugin) {
        this.plugin = plugin;
    }

    public void registerScore(final Score score) {
        scoreboards.put(score.getId(), score);
    }
    public Score getByPlayer(Player player) {
        return scoreboards.values().stream().filter(sc -> sc.getPlayers().contains(player)).findFirst().orElse(null);
    }
    public void unregisterPlugin(Plugin plugin) {

        cancelTask();

        scoreboards.values().removeIf(score -> score.getPlugin().equals(plugin));

    }
    public Score getById(String id) {
        return scoreboards.get(id);
    }

    public void removePlayers() {
        scoreboards.values().forEach(Score::removeAllPlayers);
    }
    public void removePlayer(Player player) {
        scoreboards.values().forEach(sc -> sc.removePlayer(player));
    }

    public void cancelTask() {
        if (taskId == null) return;
        plugin.getTaskManager().cancelTask(this.taskId);
        taskId = null;
    }

    public void initTask() {
        if (taskId != null) return;
        AtomicLong counter = new AtomicLong();
        this.taskId = plugin.getTaskManager().addRepeatingTask(plugin, "scores", () -> {
                scoreboards.values().forEach(score -> {
                            Bukkit.getOnlinePlayers().forEach(p -> {
                                if (score.getCondition().verify(p)) score.addPlayer(p);
                                else if (score.getPlayers().contains(p.getUniqueId())) score.removePlayer(p);
                            });

                            if (counter.get() % score.getDelay() == 0) {
                                score.update();
                                score.incrementIndex();
                            }

                        }
                );
                counter.incrementAndGet();

        },1);
    }

}
