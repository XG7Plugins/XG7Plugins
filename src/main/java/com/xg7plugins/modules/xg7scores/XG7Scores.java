package com.xg7plugins.modules.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class XG7Scores implements Module {

    @Getter
    private static XG7Scores instance;

    private final ConcurrentHashMap<String, Score> scores = new ConcurrentHashMap<>();

    private final List<UUID> players = new ArrayList<>();

    @Override
    public void onInit() {

        instance = this;

        XG7Plugins.getInstance().getDebug().loading("XG7Scores initialized");
    }

    @Override
    public void onDisable() {
        XG7Plugins.getInstance().getDebug().loading("Disabling XG7Scores");
        scores.values().forEach(Score::removeAllPlayers);
        XG7Plugins.taskManager().cancelTask("score-task");
        XG7Plugins.getInstance().getDebug().loading("XG7Scores disabled");
    }

    @Override
    public List<Task> loadTasks() {
        AtomicLong counter = new AtomicLong();
        return Collections.singletonList(new Task(
                XG7Plugins.getInstance(),
                "score-task",
                true,
                true,
                1,
                TaskState.IDLE,
                () -> {
                    scores.values().forEach(score -> {
                        players.forEach(uuid -> {
                            try {

                                Player p = Bukkit.getPlayer(uuid);

                                if (p == null) return;


                                if (score.getCondition().apply(p) && !p.isDead() && XG7Plugins.getInstance().isEnabled()) score.addPlayer(p);
                                else if (score.getPlayers().contains(p.getUniqueId())) {
                                    if (!XG7Plugins.getInstance().isEnabled()) return;
                                    Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> score.removePlayer(p));
                                }

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
        ));
    }

    @Override
    public List<Listener> loadListeners() {
        return Collections.singletonList(new ScoreListener());
    }

    @Override
    public String getName() {
        return "XG7Scores";
    }

    public void registerScore(Score score) {
        if (score == null) return;
        scores.put(score.getId(), score);
    }
    public void registerScores(Score... scores) {
        Arrays.stream(scores).forEach(this::registerScore);
    }

    public void unregisterScore(Score score) {
        scores.remove(score.getId());
    }
    public void unregisterScore(String score) {
        scores.remove(score);
    }

    public <T extends Score> T getScore(String id) {
        return (T) scores.get(id);
    }

    public List<Score> getScoresByPlayer(Player player) {
        return scores.values().stream().filter(score -> score.getPlayers().contains(player.getUniqueId())).collect(Collectors.toList());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        scores.values().forEach(score -> score.removePlayer(player));
    }

    public void disable() {
        scores.values().forEach(Score::removeAllPlayers);
        XG7Plugins.taskManager().cancelTask("score-task");
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }
}