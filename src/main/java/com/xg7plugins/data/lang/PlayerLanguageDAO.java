package com.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.data.database.Query;
import com.xg7plugins.utils.text.Text;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerLanguageDAO {

    private Cache<UUID,PlayerLanguage> playerLanguages;

    public PlayerLanguageDAO(Plugin plugin) {
        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");

        playerLanguages = Caffeine.newBuilder()
                .expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS)
                .build();
    }

    public void addPlayerLanguage(PlayerLanguage playerLanguage) {
        if(playerLanguage == null || playerLanguage.getPlayerUUID() == null) return;
        if (playerLanguages.asMap().containsKey(playerLanguage.getPlayerUUID())) return;

        EntityProcessor.insetEntity(XG7Plugins.getInstance(), playerLanguage);
        playerLanguages.put(playerLanguage.getPlayerUUID(),playerLanguage);
    }

    public PlayerLanguage getLanguage(UUID uuid) {
        if (uuid == null) return null;

        PlayerLanguage playerLanguage =  playerLanguages.getIfPresent(uuid);
        if (playerLanguage != null) return playerLanguage;

        try {
            playerLanguage = Query.getEntity(XG7Plugins.getInstance(), "SELECT * FROM playerlanguage WHERE playeruuid = ?", uuid, PlayerLanguage.class).get();
            if (playerLanguage != null){
                playerLanguages.put(uuid,playerLanguage);
                return playerLanguage;
            }
            return null;
        } catch (Exception exception) {
            return null;
        }
    }
    public CompletableFuture<Void> updatePlayerLanguage(String lang, UUID playerUUID)
    {
        if (playerUUID == null) return null;
        playerLanguages.put(playerUUID,new PlayerLanguage(playerUUID,lang));
        return Query.update(XG7Plugins.getInstance(), "UPDATE playerlanguage SET langid = ? WHERE playeruuid = ?", lang, playerUUID);


    }

}
