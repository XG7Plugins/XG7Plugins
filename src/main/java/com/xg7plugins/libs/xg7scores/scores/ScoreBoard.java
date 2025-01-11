package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


import java.util.*;
public class ScoreBoard extends Score {

    private List<String> lines;

    private HashMap<UUID, PlayerBoard> playerBoards = new HashMap<>();


    public ScoreBoard(String title, List<String> lines, String id, ScoreCondition condition, long delay, Plugin plugin) {
        super(delay, Collections.singletonList(title),id, condition, plugin);
        this.lines = lines;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);

    }

    public ScoreBoard(List<String> title, List<String> lines, String id, ScoreCondition condition, long taskDelay, Plugin plugin) {
        super(taskDelay, title,id,condition,plugin);
        this.lines = lines;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    public void update() {
        for (UUID uuid : super.getPlayers()) {

            PlayerBoard playerBoard = playerBoards.get(uuid);
            playerBoard.update();
        }
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (!super.getPlayers().contains(player.getUniqueId())) {
            super.addPlayer(player);
            playerBoards.put(player.getUniqueId(), new PlayerBoard(player, super.updateText, lines));
        }
    }

    @Override
    public synchronized void removePlayer(Player player) {
        super.removePlayer(player);
        playerBoards.remove(player.getUniqueId());

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private class PlayerBoard {
        private Scoreboard scoreboard;
        private Objective objective;
        private final List<String> title;
        private List<String> lines;
        private final HashMap<Integer, Pair<String, String>> lastLines;
        private final Player player;

        public PlayerBoard(Player player, List<String> title, List<String> lines) {
            this.player = player;
            this.title = title;
            this.lastLines = new HashMap<>();
            this.lines = lines;
            Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                this.objective = scoreboard.registerNewObjective(getId(), "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(title.get(0));

                for (int i = 0; i < lines.size(); i++) {

                    lastLines.put(i,Pair.of("Loading... " + i,"Loading... " + i));


                    Team team = scoreboard.registerNewTeam("team_" + i);
                    team.setPrefix("P ");
                    team.setSuffix(" S");

                    objective.getScore("Loading... " + i).setScore(lines.size() - i);

                }
                player.setScoreboard(scoreboard);

                update();
            });

        }

        public void update() {
            if (scoreboard == null) return;
            List<String> lastEntries = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String translatedText = Text.detectLangOrText(XG7Plugins.getInstance(),player,lines.get(i)).join().getText();

                String prefix = translatedText.substring(0, Math.min(translatedText.length(), 16));
                String entry = translatedText.length() > 16 ? translatedText.substring(16, Math.min(translatedText.length(), 56)) : "";
                String suffix = translatedText.length() > 56 ? translatedText.substring(56, Math.min(translatedText.length(), XG7Plugins.getMinecraftVersion() > 12 ? translatedText.length() : 72)) : "";

                if (!lastLines.containsKey(i) || !lastLines.get(i).equals(translatedText)) {
                    Team team = scoreboard.getTeam("team_" + i);
                    if (team == null) {
                        team = scoreboard.registerNewTeam("team_" + i);
                    }

                    if (lastLines.containsKey(i)) {
                        scoreboard.resetScores(lastLines.get(i).getSecond());
                    }

                    team.setPrefix(prefix);
                    team.setSuffix(suffix);

                    while (lastEntries.contains(entry)) {
                        entry = "§r" + ChatColor.getLastColors(prefix) + entry;
                    }


                    team.addEntry(entry);
                    objective.getScore(entry).setScore(lines.size() - i);

                    lastLines.put(i, Pair.of(translatedText,entry));
                    lastEntries.add(entry);

                }
            }

            objective.setDisplayName(Text.detectLangOrText(XG7Plugins.getInstance(),player,title.get(indexUpdating)).join().getText());
        }


    }
}