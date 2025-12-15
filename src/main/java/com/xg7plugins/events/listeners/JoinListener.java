package com.xg7plugins.events.listeners;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.lang.Lang;
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
            PlayerData loaded = XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).get(event.getUniqueId());
            if (loaded == null) {
                XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).add(new PlayerData(event.getUniqueId(), null));
            }
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,  e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        //This will have already been loaded in most cases.
        PlayerData data = XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).get(event.getPlayer().getUniqueId());

        if (data.getLangId() == null) {
            data.setLangId(XG7Plugins.getAPI().langManager().getNewLangFor(event.getPlayer()));

            //Put in cache first in error cases
            XG7Plugins.getAPI().database().cacheEntity(XG7Plugins.getInstance(), data.getLangId(), data);
            XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).updateAsync(data);

        }

        try {
            XG7Plugins.getInstance().getVersionChecker().notify(Collections.singletonList(event.getPlayer()), XG7Plugins.getAPI().getAllXG7Plugins());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
