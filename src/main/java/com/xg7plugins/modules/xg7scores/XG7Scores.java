package com.xg7plugins.modules.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7scores.organizer.TabListSorter;
import com.xg7plugins.modules.xg7scores.organizer.TabListRule;
import com.xg7plugins.modules.xg7scores.organizer.impl.NoPermRule;
import com.xg7plugins.modules.xg7scores.tasks.ScoreTimerTask;
import com.xg7plugins.modules.xg7scores.tasks.TabListSorterTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class XG7Scores implements Module {

    private boolean enabled;

    private final ConcurrentHashMap<String, Score> scores = new ConcurrentHashMap<>();

    private final List<UUID> players = new ArrayList<>();

    private final TabListSorter organizer = new TabListSorter(new ArrayList<>());

    @Override
    public void onInit() {

        XG7Plugins.getInstance().getDebug().info("scores", "XG7Scores initialized");

        organizer.addRule(new NoPermRule());
    }

    @Override
    public void onDisable() {
        XG7Plugins.getInstance().getDebug().info("scores", "Disabling XG7Scores");
        scores.values().forEach(Score::removeAllPlayers);
        XG7Plugins.getAPI().taskManager().cancelRepeatingTask(XG7Plugins.getPluginID("score-task"));
        XG7Plugins.getInstance().getDebug().info("scores", "XG7Scores disabled");
    }

    @Override
    public void onReload() {
        scores.values().forEach(Score::removeAllPlayers);
    }

    public void registerTablistOrgaizerRule(TabListRule rule) {
        organizer.addRule(rule);
    }

    @Override
    public List<TimerTask> loadTasks() {

        return Arrays.asList(new ScoreTimerTask(this), new TabListSorterTask(this));
    }

    @Override
    public List<Listener> loadListeners() {
        return Collections.singletonList(new ScoreListener());
    }

    @Override
    public String getName() {
        return "XG7Scores";
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void registerScore(Score score) {
        if (score == null) return;
        scores.put(score.getId(), score);
    }
    public void registerScores(List<Score> scores) {
        if (scores == null) return;
        scores.forEach(this::registerScore);
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
        scores.values().forEach(score -> score.removePlayer(player));
        players.remove(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public static void loadScores(Score... scores) {
        XG7Plugins.getAPI().scores().registerScores(Arrays.asList(scores));
    }

    public static void unloadScore(String id) {
        XG7Plugins.getAPI().scores().unregisterScore(id);
    }
}