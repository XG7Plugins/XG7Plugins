package com.xg7plugins.data.playerdata;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;
import com.xg7plugins.utils.DAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerDataDAO implements DAO<UUID, PlayerData> {

    @Override
    public CompletableFuture<Boolean> add(PlayerData data) throws ExecutionException, InterruptedException {
        if(data == null || data.getPlayerUUID() == null) return CompletableFuture.completedFuture(null);

        return XG7Plugins.getInstance().getDatabaseManager().getProcessor().exists(
                XG7Plugins.getInstance(), PlayerData.class, "player_id", data.getPlayerUUID()
        ).thenApply(exists -> {
            if (exists) return false;
            try {
                Transaction.createTransaction(
                        XG7Plugins.getInstance(),
                        data,
                        Transaction.Type.INSERT
                ).waitForResult();
                return true;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });


    }

    @Override
    public CompletableFuture<PlayerData> get(UUID uuid) {
        if (uuid == null) return null;

        return XG7Plugins.getInstance().getDatabaseManager().containsCachedEntity(XG7Plugins.getInstance(), uuid.toString()).thenComposeAsync(exists -> {
            if (exists) return XG7Plugins.getInstance().getDatabaseManager().getCachedEntity(XG7Plugins.getInstance(), uuid.toString());

            try {
                return CompletableFuture.completedFuture(Query.selectFrom(XG7Plugins.getInstance(), PlayerData.class, uuid).onError(Throwable::printStackTrace).waitForResult().get(PlayerData.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } ,XG7Plugins.taskManager().getAsyncExecutors().get("database"));


    }

    @Override
    public CompletableFuture<Boolean> update(PlayerData data) {
        if (data == null) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction.update(XG7Plugins.getInstance(), data).onError(Throwable::printStackTrace).waitForResult();
                XG7Plugins.getInstance().getDatabaseManager().cacheEntity(XG7Plugins.getInstance(), data.getPlayerUUID().toString(), data);
                return true;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));

    }
}
