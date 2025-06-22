package com.xg7plugins.modules.xg7scores;

import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        XG7Scores.getInstance().removePlayer(event.getPlayer());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        XG7Scores.getInstance().addPlayer(event.getPlayer());
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
