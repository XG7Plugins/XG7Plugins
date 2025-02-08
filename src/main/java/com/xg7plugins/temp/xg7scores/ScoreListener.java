package com.xg7plugins.temp.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

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
