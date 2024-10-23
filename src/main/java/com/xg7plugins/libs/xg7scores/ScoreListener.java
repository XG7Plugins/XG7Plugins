package com.xg7plugins.libs.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreListener implements Event {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Score score = XG7Plugins.getInstance().getScoreManager().getByPlayer(event.getPlayer());
        if (score == null) return;
        score.removePlayer(event.getPlayer());
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
