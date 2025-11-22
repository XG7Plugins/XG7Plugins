package com.xg7plugins.modules.xg7npcs.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class NPCListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        XG7Plugins.getAPI().npcs().unregisterAllLivingNPCs(player.getUniqueId());
    }
}
