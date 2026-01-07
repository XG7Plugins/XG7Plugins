package com.xg7plugins.modules.xg7holograms.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        XG7Plugins.getAPI().holograms().unregisterAllLivingHolograms(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        XG7Plugins.getAPI().holograms().getRegisteredHolograms().values()
                .stream().filter(hologram -> hologram.getLocation().getWorld().getUID() == player.getWorld().getUID())
                .forEach(hologram -> hologram.spawn(player));
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        XG7Plugins.getAPI().holograms().getRegisteredHolograms().values()
                .stream().filter(hologram -> hologram.getLocation().getWorld().getUID() == player.getWorld().getUID())
                .forEach(hologram -> hologram.spawn(player));
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (event.getTo() == null) {
            return;
        }
        if (event.getFrom().getWorld().getUID() == event.getTo().getWorld().getUID()) {
            return;
        }

        XG7Plugins.getAPI().holograms().unregisterAllLivingHolograms(player.getUniqueId());

        XG7Plugins.getAPI().holograms().getRegisteredHolograms().values()
                .stream().filter(hologram -> hologram.getLocation().getWorld().getUID() == player.getWorld().getUID())
                .forEach(hologram -> hologram.spawn(player));
    }

}
