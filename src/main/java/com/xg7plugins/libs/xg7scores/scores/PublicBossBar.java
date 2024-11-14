package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class PublicBossBar extends GenericBossBar {
    
    private final BossBar bossBar;
    
    public PublicBossBar(long delay, String[] title, String id, ScoreCondition condition, BarColor color, BarStyle style, double progress, Plugin plugin) {
        super(delay, title, id, condition, plugin);

        bossBar = Bukkit.createBossBar(title[0],color,style);
        bossBar.setProgress(progress);
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @SneakyThrows
    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);

    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        bossBar.removePlayer(player);
    }

    @Override
    public void update() {
        bossBar.setTitle(Text.format(getToUpdate()[getIndexUpdating()],plugin).getText());
    }
}
