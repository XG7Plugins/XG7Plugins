package com.xg7plugins.libs.xg7scores;


import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Getter
public class ScoreManager {

    private final XG7Plugins plugin;

    private final ConcurrentHashMap<String, Score> scoreboards = new ConcurrentHashMap<>();
    private final List<UUID> sendActionBlackList = new ArrayList<>();

    private final Task task;

    public ScoreManager(XG7Plugins plugin) {
        this.plugin = plugin;
        AtomicLong counter = new AtomicLong();
        this.task = new Task(
                plugin,
                "score-task",
                true,
                true,
                1,
                TaskState.IDLE,
                () -> {
                    scoreboards.values().forEach(score -> {
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            try {
                                if (p == null) return;
                                if (score.getCondition().verify(p)) score.addPlayer(p);
                                else if (score.getPlayers().contains(p.getUniqueId())) score.removePlayer(p);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        try {
                            if (counter.get() % score.getDelay() == 0) {
                                score.update();
                                score.incrementIndex();
                            }

                            if (counter.get() == Long.MAX_VALUE) counter.set(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    });
                    counter.incrementAndGet();
                }
        );
    }
    public void registerScores(final Score[] scores) {
        if (scores == null) return;
        scoreboards.putAll(Arrays.stream(scores).map(sc -> new AbstractMap.SimpleEntry<>(sc.getId(), sc)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public void registerScore(final Score score) {
        scoreboards.put(score.getId(), score);
    }
    public List<Score> getByPlayer(Player player) {
        return scoreboards.values().stream().filter(sc -> sc.getPlayers().contains(player.getUniqueId())).collect(Collectors.toList());
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
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
        XG7Plugins.taskManager().cancelTask(task);
    }

}
