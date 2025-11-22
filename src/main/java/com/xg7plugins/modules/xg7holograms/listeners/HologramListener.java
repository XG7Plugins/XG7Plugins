package com.xg7plugins.modules.xg7holograms.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class HologramListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        XG7Plugins.getAPI().holograms().unregisterAllLivingHolograms(player.getUniqueId());
    }
}
