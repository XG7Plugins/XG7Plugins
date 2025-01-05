package com.xg7plugins.data.lang;

import com.xg7plugins.XG7Plugins;

import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;
import com.xg7plugins.utils.DAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerLanguageDAO extends DAO<UUID, PlayerLanguage> {

    public CompletableFuture<Void> add(PlayerLanguage playerLanguage) throws ExecutionException, InterruptedException {
        if(playerLanguage == null || playerLanguage.getPlayerUUID() == null) return CompletableFuture.completedFuture(null);

        return XG7Plugins.getInstance().getDatabaseManager().getProcessor().exists(
                XG7Plugins.getInstance(), PlayerLanguage.class, "player_id", playerLanguage.getPlayerUUID()
        ).thenAccept(exists -> {
            if (exists) return;
            try {
                Transaction.createTransaction(
                        XG7Plugins.getInstance(),
                        playerLanguage,
                        Transaction.Type.INSERT
                ).queue().waitForResult();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public CompletableFuture<PlayerLanguage> get(UUID uuid) {
        if (uuid == null) return null;

        if (XG7Plugins.getInstance().getDatabaseManager().containsCachedEntity(uuid.toString())) {
            return CompletableFuture.completedFuture(XG7Plugins.getInstance().getDatabaseManager().getCachedEntity(uuid.toString()));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Query.selectFrom(XG7Plugins.getInstance(), PlayerLanguage.class, uuid).onError(Throwable::printStackTrace).queue().waitForResult().get(PlayerLanguage.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));

    }
    public CompletableFuture<Void> update(PlayerLanguage playerLanguage) {
        if (playerLanguage == null) return null;

        return CompletableFuture.runAsync(() -> {
            try {
                Transaction.update(XG7Plugins.getInstance(), playerLanguage).onError(Throwable::printStackTrace).queue().waitForResult();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));

    }

}
