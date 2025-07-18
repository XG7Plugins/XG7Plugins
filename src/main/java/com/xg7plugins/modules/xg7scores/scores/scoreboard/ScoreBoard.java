package com.xg7plugins.modules.xg7scores.scores.scoreboard;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

            PlayerBoard playerBoard = new PlayerBoard(this, healthDisplaySuffix, player, super.updateText, lines, sideBar, belowName);

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
}