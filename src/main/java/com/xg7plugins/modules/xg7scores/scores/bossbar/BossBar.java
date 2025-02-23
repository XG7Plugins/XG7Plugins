package com.xg7plugins.modules.xg7scores.scores.bossbar;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class BossBar extends Score {

    private final Map<UUID, org.bukkit.boss.BossBar> bossBars = new HashMap<>();

    private final BarColor color;
    private final BarStyle style;
    private final double progress;

    public BossBar(long delay, String id, Function<Player, Boolean> condition, List<String> title, BarColor color, BarStyle style, double progress, Plugin plugin) {
        super(delay, title, id, condition, plugin);
        this.color = color;
        this.style = style;
        this.progress = progress;
    }
    @Override
    public synchronized void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBars.containsKey(player.getUniqueId())) {
            bossBars.put(player.getUniqueId(), Bukkit.createBossBar(updateText.get(0),color,style));
            bossBars.get(player.getUniqueId()).setProgress(progress / 100);
            bossBars.get(player.getUniqueId()).addPlayer(player);
        }

    }

    @Override
    public void removePlayer(Player player) {
        if (!bossBars.containsKey(player.getUniqueId())) return;
        super.removePlayer(player);
        bossBars.get(player.getUniqueId()).removePlayer(player);
        bossBars.remove(player.getUniqueId());

    }
    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            String name = Text.detectLangs(player, plugin,updateText.get(indexUpdating)).join().getPlainText();

            if (!bossBars.get(id).getTitle().equals(name)) {
                bossBars.get(player.getUniqueId()).setTitle(name);
            }
        }
    }
}
