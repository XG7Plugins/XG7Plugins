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
    public CompletableFuture<Void> add(PlayerData data) throws ExecutionException, InterruptedException {
        if(data == null || data.getPlayerUUID() == null) return CompletableFuture.completedFuture(null);

        return XG7Plugins.getInstance().getDatabaseManager().getProcessor().exists(
                XG7Plugins.getInstance(), PlayerData.class, "player_id", data.getPlayerUUID()
        ).thenAccept(exists -> {
            if (exists) return;
            try {
                Transaction.createTransaction(
                        XG7Plugins.getInstance(),
                        data,
                        Transaction.Type.INSERT
                ).waitForResult();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });


    }

    @Override
    public CompletableFuture<PlayerData> get(UUID uuid) {
        if (uuid == null) return null;

        return XG7Plugins.getInstance().getDatabaseManager().containsCachedEntity(uuid.toString()).thenComposeAsync(exists -> {
            if (exists) return XG7Plugins.getInstance().getDatabaseManager().getCachedEntity(uuid.toString());

            try {
                return CompletableFuture.completedFuture(Query.selectFrom(XG7Plugins.getInstance(), PlayerData.class, uuid).onError(Throwable::printStackTrace).waitForResult().get(PlayerData.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } ,XG7Plugins.taskManager().getAsyncExecutors().get("database"));


    }

    @Override
    public CompletableFuture<Void> update(PlayerData data) {
        if (data == null) return CompletableFuture.completedFuture(null);

        return CompletableFuture.runAsync(() -> {
            try {
                Transaction.update(XG7Plugins.getInstance(), data).onError(Throwable::printStackTrace).waitForResult();
                XG7Plugins.getInstance().getDatabaseManager().cacheEntity(data.getPlayerUUID().toString(), data);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("database"));

    }
}
