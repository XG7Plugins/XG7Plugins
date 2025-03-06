package com.xg7plugins.modules.xg7scores.scores;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


import java.util.*;
import java.util.function.Function;

public class ScoreBoard extends Score {

    private final List<String> lines;

    private final HashMap<UUID, PlayerBoard> playerBoards = new HashMap<>();

    private final boolean belowName;
    private final boolean sideBar;
    private final String healthDisplaySuffix;


    public ScoreBoard(String title, List<String> lines, String id, Function<Player, Boolean> condition, long delay, Plugin plugin, boolean belowName, boolean sideBar, String healthDisplaySuffix) {
        super(delay, Collections.singletonList(title),id, condition, plugin);
        this.lines = lines;

        this.belowName = belowName;
        this.sideBar = sideBar;
        this.healthDisplaySuffix = healthDisplaySuffix;
    }

    public ScoreBoard(List<String> title, List<String> lines, String id, Function<Player, Boolean> condition, long taskDelay, Plugin plugin, boolean belowName, boolean sideBar, String healthDisplaySuffix) {
        super(taskDelay, title,id,condition,plugin);
        this.lines = lines;
        this.belowName = belowName;
        this.sideBar = sideBar;
        this.healthDisplaySuffix = healthDisplaySuffix;
    }

    public void update() {
        for (UUID uuid : super.getPlayers()) {

            PlayerBoard playerBoard = playerBoards.get(uuid);

            if (playerBoard == null) continue;

            if (sideBar) playerBoard.updateSidebar();
            if (belowName) playerBoard.updateBelowName();
        }
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (!super.getPlayers().contains(player.getUniqueId())) {
            super.addPlayer(player);

            PlayerBoard playerBoard = new PlayerBoard(healthDisplaySuffix, player, super.updateText, lines);

            if (sideBar) playerBoard.createSidebar();
            if (belowName) playerBoard.createBelowname();

            playerBoards.put(player.getUniqueId(), playerBoard);

        }
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;
        super.removePlayer(player);
        playerBoards.remove(player.getUniqueId());

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private class PlayerBoard {
        private Scoreboard scoreboard;
        private Objective sidebarObjective;
        private Objective belowNameObjective;

        private final List<String> title;
        private final List<String> lines;
        private final HashMap<Integer, Pair<String, String>> lastLines;

        private final String healthDisplaySuffix;

        private final Player player;

        public PlayerBoard(String healthDisplaySuffix, Player player, List<String> title, List<String> lines) {
            this.healthDisplaySuffix = healthDisplaySuffix;
            this.player = player;
            this.title = title;
            this.lastLines = new HashMap<>();
            this.lines = lines;
            Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
            });

        }

        public void createSidebar() {
            if (sidebarObjective != null) return;
            XG7Plugins.taskManager().runSyncTask(XG7Plugins.getInstance(), () -> {
                this.sidebarObjective = scoreboard.registerNewObjective("sb-" + getId(), "dummy");
                sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                sidebarObjective.setDisplayName(title.get(0));

                for (int i = 0; i < lines.size(); i++) {

                    lastLines.put(i, Pair.of("Loading... " + i, "Loading... " + i));

                    Team team = scoreboard.registerNewTeam("team_" + i);
                    team.addEntry("Loading... " + i);
                    team.setPrefix("P ");
                    team.setSuffix(" S");

                    sidebarObjective.getScore("Loading... " + i).setScore(lines.size() - i);
                }

                updateSidebar();
            });
        }

        public void createBelowname() {
            XG7Plugins.taskManager().runSyncTask(XG7Plugins.getInstance(), () -> {
                this.belowNameObjective = scoreboard.registerNewObjective("bn-" + getId(), "health");

                belowNameObjective.setDisplayName(Text.detectLangs(player, plugin, healthDisplaySuffix).join().getText());

                belowNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

                updateBelowName();
            });
        }

        public void updateBelowName() {
            if (scoreboard == null) return;
            if (belowNameObjective == null) return;

            try {
                int score = (int) player.getHealth();
                belowNameObjective.getScore(player.getName()).setScore(score);
                belowNameObjective.setDisplayName(Text.detectLangs(player, plugin, healthDisplaySuffix).join().getText());
            } catch (Exception e) {
                XG7Plugins.getInstance().getDebug().warn("Error while updating belowname for player " + player.getName());
                XG7Plugins.getInstance().getDebug().warn("Check if the placeholder is right!");
            }
        }

        public void updateSidebar() {
            if (scoreboard == null) return;
            if (sidebarObjective == null) return;
            List<String> lastEntries = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String translatedText = Text.detectLangs(player, plugin,lines.get(i)).join().getPlainText();

                String prefix = translatedText.substring(0, Math.min(translatedText.length(), 16));
                String entry = translatedText.length() > 16 ? translatedText.substring(16, Math.min(translatedText.length(), 56)) : "";
                String suffix = translatedText.length() > 56 ? translatedText.substring(56, Math.min(translatedText.length(), MinecraftVersion.isNewerThan(12) ? translatedText.length() : 72)) : "";

                if (MinecraftVersion.isNewerOrEqual(13)) {
                    suffix = ChatColor.getLastColors(prefix) + entry + suffix;
                    entry = "";
                }
                while (lastEntries.contains(entry)) {
                    entry += "§r" + ChatColor.getLastColors(prefix);
                }
                lastEntries.add(entry);
                if (lastLines.get(i).getFirst().equals(translatedText)) continue;

                Team team = scoreboard.getTeam("team_" + i);
                if (team == null) {
                    team = scoreboard.registerNewTeam("team_" + i);
                }

                scoreboard.resetScores(lastLines.get(i).getSecond());

                team.addEntry(entry);

                team.setPrefix(prefix);
                team.setSuffix(suffix);

                sidebarObjective.getScore(entry).setScore(lines.size() - i);

                lastLines.put(i, Pair.of(translatedText,entry));
            }

            sidebarObjective.setDisplayName(Text.detectLangs(player, plugin,title.get(indexUpdating)).join().getText());
        }

    }
}