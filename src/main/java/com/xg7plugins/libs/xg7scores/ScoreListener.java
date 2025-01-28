package com.xg7plugins.libs.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.libs.xg7scores.scores.bossbar.LegacyBossBar;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class ScoreListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        XG7Plugins.getInstance().getScoreManager().removePlayer(event.getPlayer());
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
