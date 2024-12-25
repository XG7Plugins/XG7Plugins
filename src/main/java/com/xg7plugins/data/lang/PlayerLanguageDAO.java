package com.xg7plugins.data.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.data.database.Query;
import com.xg7plugins.utils.DAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerLanguageDAO extends DAO<UUID, PlayerLanguage> {

    public CompletableFuture<Void> add(PlayerLanguage playerLanguage) throws ExecutionException, InterruptedException {
        if(playerLanguage == null || playerLanguage.getPlayerUUID() == null) return CompletableFuture.completedFuture(null);
        return EntityProcessor.exists(XG7Plugins.getInstance(),PlayerLanguage.class, "playerUUID", playerLanguage.getPlayerUUID()).thenAccept(r -> {
            if (!r) EntityProcessor.insetEntity(XG7Plugins.getInstance(), playerLanguage);
        });
    }

    public CompletableFuture<PlayerLanguage> get(UUID uuid) {
        if (uuid == null) return null;

        return Query.getEntity(XG7Plugins.getInstance(), "SELECT * FROM playerlanguage WHERE playeruuid = ?", uuid, PlayerLanguage.class);
    }
    public CompletableFuture<Void> update(PlayerLanguage playerLanguage) {
        if (playerLanguage == null) return null;

        return Query.update(XG7Plugins.getInstance(), playerLanguage);
    }

}
