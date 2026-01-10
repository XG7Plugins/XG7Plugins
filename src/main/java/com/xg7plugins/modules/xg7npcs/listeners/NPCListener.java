package com.xg7plugins.modules.xg7npcs.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NPCListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        killFor(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        killFor(event.getEntity());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(() -> spawnFor(event.getPlayer())), 50L);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        spawnFor(event.getPlayer());
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
        killFor(player);
        spawnFor(player);
    }

    private void spawnFor(Player player) {
        XG7Plugins.getAPI().npcs().getRegisteredNPCs().values()
                .stream().filter(npc -> npc.getSpawnLocation().getWorld().getUID() == player.getWorld().getUID())
                .forEach(npc -> {
                    System.out.println("Spawning NPC " + npc.getId() + " for " + player.getName());
                    npc.spawn(player);
                });
    }

    private void killFor(Player player) {
        XG7Plugins.getAPI().npcs().unregisterAllLivingNPCs(player.getUniqueId());
    }
}
