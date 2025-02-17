package com.xg7plugins.modules.xg7scores.scores;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class XPBar extends Score {

    public XPBar(long delay, List<String> numbers, String id, Function<Player, Boolean> condition, Plugin plugin) {
        super(delay, numbers, id, condition, plugin);
    }

    @Override
    public void update() {
            for (UUID id : super.getPlayers()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) continue;

                String level = updateText.get(indexUpdating).split(", ")[0];
                String progress = updateText.get(indexUpdating).split(", ")[1];

                level = Text.format(level).textFor(player).getText();
                progress = Text.format(progress).textFor(player).getText();


                player.setLevel(Integer.parseInt(level));
                player.setExp(Float.parseFloat(progress));
            }
    }
}
