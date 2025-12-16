package com.xg7plugins.modules.xg7scores;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7scores.organizer.TabListSorter;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        XG7Plugins.getAPI().scores().addPlayer(event.getPlayer());

        if (!ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("organize-tablist", true)) return;

        TabListSorter sorter = XG7Plugins.getAPI().scores().getOrganizer();

        sorter.createAllTeamsForPlayer(event.getPlayer());
        sorter.addPlayer(event.getPlayer());
        sorter.addToUpdateList(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        XG7Plugins.getAPI().scores().removePlayer(event.getPlayer());

        if (!ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("organize-tablist", true)) return;

        TabListSorter sorter = XG7Plugins.getAPI().scores().getOrganizer();

        sorter.removePlayer(event.getPlayer());
        sorter.deleteAllTeamsForPlayer(event.getPlayer());
        sorter.removeFromUpdateList(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
