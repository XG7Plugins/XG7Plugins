package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class XPBar extends Score {

    public XPBar(long delay, String[] numbers, String id, ScoreCondition condition, Plugin plugin) {
        super(delay, numbers, id, condition, plugin);
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
            for (UUID id : super.getPlayers()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) continue;

                String level = getToUpdate()[getIndexUpdating()].split(", ")[0];
                String progress = getToUpdate()[getIndexUpdating()].split(", ")[1];

                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    level = PlaceholderAPI.setPlaceholders(player, level);
                    progress = PlaceholderAPI.setPlaceholders(player, progress);
                }

                player.setLevel(Integer.parseInt(level));
                player.setExp(Float.parseFloat(progress));
            }
    }
}
