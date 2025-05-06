package com.xg7plugins.events.defaultevents;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ExecutionException;

@Listener
public class JoinListener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {

        XG7Plugins plugin = XG7Plugins.getInstance();

        try {
            plugin.getPlayerDataDAO().add(new PlayerData(event.getUniqueId(), null));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        XG7Plugins plugin = XG7Plugins.getInstance();
        try {
            plugin.getPlayerDataDAO().get(event.getPlayer().getUniqueId()).thenAccept(playerData -> {
                if (playerData == null) return;

                if (playerData.getLangId() == null) {
                    playerData.setLangId(plugin.getLangManager().getNewLangFor(event.getPlayer()).join());
                    plugin.getPlayerDataDAO().update(playerData);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
