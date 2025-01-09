package com.xg7plugins.libs.xg7scores.scores.bossbar;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class PublicBossBar extends Score {
    
    private final BossBar bossBar;
    
    public PublicBossBar(long delay, List<String> title, String id, ScoreCondition condition, BarColor color, BarStyle style, double progress, Plugin plugin) {
        super(delay, title, id, condition, plugin);

        bossBar = Bukkit.createBossBar(title.get(0),color,style);
        bossBar.setProgress(progress);
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @SneakyThrows
    @Override
    public synchronized void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);

    }

    @SneakyThrows
    @Override
    public synchronized void removePlayer(Player player) {
        super.removePlayer(player);
        bossBar.removePlayer(player);
    }

    @Override
    public void update() {
        bossBar.setTitle(Text.format(updateText.get(indexUpdating),plugin).getText());
    }
}
