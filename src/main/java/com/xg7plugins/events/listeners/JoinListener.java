package com.xg7plugins.events.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;

public class JoinListener implements Listener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {

        try {
            XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).add(new PlayerData(event.getUniqueId(), null));
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,  e.getMessage());
            throw new RuntimeException(e);
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        try {
            XG7Plugins.getAPI().requestPlayerData(event.getPlayer()).thenAccept(playerData -> {
                if (playerData == null) return;

                if (playerData.getLangId() == null) {
                    playerData.setLangId(XG7Plugins.getAPI().langManager().getNewLangFor(event.getPlayer()).join());
                    XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).update(playerData);
                }
            });

            if (event.getPlayer().isOp()) {
                XG7Plugins.getInstance().getVersionChecker().notify(Collections.singletonList(event.getPlayer()), XG7Plugins.getAPI().getAllXG7Plugins());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
