package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar extends GenericBossBar {

    private final Map<UUID, org.bukkit.boss.BossBar> bossBars = new HashMap<>();

    private final BarColor color;
    private final BarStyle style;
    private final double progress;

    public BossBar(long delay, String id, ScoreCondition condition, String[] title, BarColor color, BarStyle style, double progress, Plugin plugin) {
        super(delay, title, id, condition, plugin);
        this.color = color;
        this.style = style;
        this.progress = progress;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }
    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBars.containsKey(player.getUniqueId())) {
            bossBars.put(player.getUniqueId(), Bukkit.createBossBar(getToUpdate()[0],color,style));
            bossBars.get(player.getUniqueId()).setProgress(progress);
            bossBars.get(player.getUniqueId()).addPlayer(player);
        }

    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        bossBars.get(player.getUniqueId()).removePlayer(player);
        bossBars.remove(player.getUniqueId());

    }
    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (!bossBars.get(id).getTitle().equals(Text.format(getToUpdate()[getIndexUpdating()], plugin).getWithPlaceholders(player))) {
                bossBars.get(player.getUniqueId()).setTitle(Text.format(getToUpdate()[getIndexUpdating()], plugin).getWithPlaceholders(player));
            }
        }
    }
}
