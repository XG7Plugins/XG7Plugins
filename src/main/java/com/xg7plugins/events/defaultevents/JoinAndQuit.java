package com.xg7plugins.events.defaultevents;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutionException;

public class JoinAndQuit implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        XG7Plugins plugin = XG7Plugins.getInstance();

        try {
            XG7Plugins.getInstance().getPlayerDataDAO().add(new PlayerData(event.getPlayer().getUniqueId(), plugin.getLangManager().getMainLang()));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        plugin.getPacketEventManager().create(event.getPlayer());
        if (XG7Plugins.getMinecraftVersion() > 7) plugin.getHologramsManager().addPlayer(event.getPlayer());
        plugin.getNpcManager().addPlayer(event.getPlayer());

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        XG7Plugins plugin = XG7Plugins.getInstance();
        plugin.getPacketEventManager().stopEvent(event.getPlayer());
        if (XG7Plugins.getMinecraftVersion() > 7) plugin.getHologramsManager().removePlayer(event.getPlayer());
        plugin.getNpcManager().removePlayer(event.getPlayer());

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScoreManager().removePlayer(event.getPlayer()), 2L);

    }
}
