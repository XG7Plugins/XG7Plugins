package com.xg7plugins.modules.xg7scores.scores.scoreboard;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PlayerBoard {
    private Scoreboard bukkitScoreboard;
    private Objective sidebarObjective;
    private Objective belowNameObjective;

    private final List<String> title;
    private final List<String> lines;
    private final HashMap<Integer, Pair<String, String>> lastLines;

    private final String healthDisplaySuffix;

    private final ScoreBoard scoreBoard;
    private final Player player;

    public PlayerBoard(ScoreBoard board, String healthDisplaySuffix, Player player, List<String> title, List<String> lines) {
        this.healthDisplaySuffix = healthDisplaySuffix;
        this.player = player;
        this.title = title;
        this.lastLines = new HashMap<>();
        this.lines = lines;
        this.scoreBoard = board;
        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Plugins.getInstance(), () -> {
            this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(bukkitScoreboard);
        }));

    }

    public void createSidebar() {
        if (sidebarObjective != null) return;
        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Plugins.getInstance(), () -> {
            this.sidebarObjective = bukkitScoreboard.registerNewObjective("sb-" + scoreBoard.getId(), "dummy");
            sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            sidebarObjective.setDisplayName(title.get(0));

            for (int i = 0; i < lines.size(); i++) {

                lastLines.put(i, Pair.of("Loading... " + i, "Loading... " + i));

                Team team = bukkitScoreboard.registerNewTeam("team_" + i);
                team.addEntry("Loading... " + i);
                team.setPrefix("P ");
                team.setSuffix(" S");

                sidebarObjective.getScore("Loading... " + i).setScore(lines.size() - i);
            }

            updateSidebar();
        }), 2000L);
    }

    public void createBelowname() {
        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Plugins.getInstance(), () -> {
            this.belowNameObjective = bukkitScoreboard.registerNewObjective("bn-" + scoreBoard.getId(), "health");

            belowNameObjective.setDisplayName(Text.detectLangs(player, scoreBoard.getPlugin(), healthDisplaySuffix).join().getText());

            belowNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

            updateBelowName();
        }));
    }

    public void updateBelowName() {
        if (bukkitScoreboard == null) return;
        if (belowNameObjective == null) return;

        try {
            int score = (int) player.getHealth();
            belowNameObjective.getScore(player.getName()).setScore(score);
            belowNameObjective.setDisplayName(Text.detectLangs(player, scoreBoard.getPlugin(), healthDisplaySuffix).join().getText());
        } catch (Exception e) {
            XG7Plugins.getInstance().getDebug().warn("Error while updating belowname for player " + player.getName());
            XG7Plugins.getInstance().getDebug().warn("Check if the placeholder is right!");
        }
    }

    public void updateSidebar() {
        if (bukkitScoreboard == null) return;
        if (sidebarObjective == null) return;
        List<String> lastEntries = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String translatedText = Text.detectLangs(player, scoreBoard.getPlugin(),lines.get(i)).join().getText();

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

            Team team = bukkitScoreboard.getTeam("team_" + i);
            if (team == null) {
                team = bukkitScoreboard.registerNewTeam("team_" + i);
            }

            bukkitScoreboard.resetScores(lastLines.get(i).getSecond());

            team.addEntry(entry);

            team.setPrefix(prefix);
            team.setSuffix(suffix);

            sidebarObjective.getScore(entry).setScore(lines.size() - i);

            lastLines.put(i, Pair.of(translatedText,entry));
        }

        sidebarObjective.setDisplayName(Text.detectLangs(player, scoreBoard.getPlugin(),title.get(scoreBoard.getIndexUpdating())).join().getText());
    }

}
