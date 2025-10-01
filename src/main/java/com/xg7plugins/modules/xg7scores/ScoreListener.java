package com.xg7plugins.modules.xg7scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7scores.organizer.TabListSorter;
import com.xg7plugins.tasks.tasks.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        XG7Scores.getInstance().removePlayer(event.getPlayer());

        if (!ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("organize-tablist", true)) return;

        XG7Scores.getInstance().getOrganizer().removePlayer(event.getPlayer());
        XG7Scores.getInstance().getOrganizer().removeFromUpdateList(event.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        XG7Scores.getInstance().addPlayer(event.getPlayer());

        if (!ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("organize-tablist", true)) return;

        TabListSorter sorter = XG7Scores.getInstance().getOrganizer();

        sorter.createAllTeamsForPlayer(event.getPlayer());
        sorter.addPlayer(event.getPlayer());
        sorter.addToUpdateList(event.getPlayer().getUniqueId());
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
