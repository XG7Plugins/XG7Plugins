package com.xg7plugins.events.listeners;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class JoinListener implements Listener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {

        try {
            XG7PluginsAPI.getRepository(PlayerDataRepository.class).add(new PlayerData(event.getUniqueId(), null));
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,  e.getMessage());
            throw new RuntimeException(e);
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        try {
            XG7PluginsAPI.requestPlayerData(event.getPlayer()).thenAccept(playerData -> {
                if (playerData == null) return;

                if (playerData.getLangId() == null) {
                    playerData.setLangId(XG7PluginsAPI.langManager().getNewLangFor(event.getPlayer()).join());
                    XG7PluginsAPI.getRepository(PlayerDataRepository.class).update(playerData);
                }
            });

            if (event.getPlayer().isOp()) XG7Plugins.getInstance().getVersionChecker().notify(Collections.singletonList(event.getPlayer()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
