package com.xg7plugins.data.dao;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Data Access Object (DAO) interface for handling database operations.
 * Provides asynchronous CRUD operations for entities.
 *
 * @param <ID> The type of the identifier used for the entity
 * @param <T>  The type of the entity being managed
 */
public interface DAO<ID, T extends Entity<?,?>> {
    /**
     * Adds a new entity to the database asynchronously.
     *
     * @param entity The entity to be added
     * @return A CompletableFuture containing true if the operation was successful, false otherwise
     * @throws ExecutionException If the computation threw an exception
     * @throws InterruptedException If the current thread was interrupted while waiting
     */
    default CompletableFuture<Boolean> add(T entity) throws ExecutionException, InterruptedException {
        if(entity == null || entity.getID() == null) return CompletableFuture.completedFuture(null);

        return XG7PluginsAPI.dbProcessor().exists(
                getPlugin(), getEntityClass(), entity.getID()
        ).thenApply(exists -> {
            if (exists) return false;
            try {
                Transaction.createTransaction(
                        getPlugin(),
                        entity,
                        Transaction.Type.INSERT
                ).waitForResult();
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });


    }

    /**
     * Retrieves an entity from the database by its ID asynchronously.
     *
     * @param id The identifier of the entity to retrieve
     * @return A CompletableFuture containing the retrieved entity
     */
    default CompletableFuture<T> get(ID id) {
        if (id == null) return CompletableFuture.completedFuture(null);

        return XG7PluginsAPI.database().containsCachedEntity(getPlugin(), id.toString()).thenComposeAsync(exists -> {
            if (exists) return XG7PluginsAPI.database().getCachedEntity(getPlugin(), id.toString());

            try {
                return CompletableFuture.completedFuture(Query.selectFrom(getPlugin(), getEntityClass(), id).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult().get(getEntityClass()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } ,XG7PluginsAPI.taskManager().getAsyncExecutors().get("database"));


    }

    default CompletableFuture<List<T>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Query.selectAllFrom(getPlugin(), getEntityClass()).waitForResult().getList(getEntityClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates an existing entity in the database asynchronously.
     *
     * @param entity The entity to be updated
     * @return A CompletableFuture containing true if the operation was successful, false otherwise
     */
    default CompletableFuture<Boolean> update(T entity) {
        if (entity == null) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction.update(getPlugin(), entity).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult();
                XG7PluginsAPI.database().cacheEntity(getPlugin(), entity.getID().toString(), entity);
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }, XG7PluginsAPI.taskManager().getAsyncExecutors().get("database"));

    }

    default CompletableFuture<Boolean> delete(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction.delete(getPlugin(), entity).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult();
                XG7PluginsAPI.database().unCacheEntity(getPlugin(), entity.getID().toString());
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    Plugin getPlugin();
    Class<T> getEntityClass();

}
