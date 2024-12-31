package com.xg7plugins.libs.xg7scores;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class Score {

    private boolean updating = false;
    private final long delay;
    private final String id;
    protected final List<String> updateText;
    protected int indexUpdating = 0;
    private final Set<UUID> players;
    private final ScoreCondition condition;

    protected Plugin plugin;

    public Score(long delay, List<String> updateText, String id, ScoreCondition condition, Plugin plugin) {
        this.delay = delay;
        this.updateText = updateText;
        this.players = new HashSet<>();
        this.id = id;
        this.condition = condition;
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        updating = true;
    }

    public void removePlayer(Player player) {

        if (!players.contains(player.getUniqueId())) return;

            players.remove(player.getUniqueId());
            if (players.isEmpty()) updating = false;
    }

    public void removeAllPlayers() {
        players.stream().map(Bukkit::getPlayer).collect(Collectors.toList()).forEach(this::removePlayer);
        updating = false;
    }

    public void incrementIndex() {
        this.indexUpdating++;
        if (indexUpdating == updateText.size()) indexUpdating = 0;
    }

    public abstract void update();

}
