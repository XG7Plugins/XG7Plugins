package com.xg7plugins.data.database.dao;

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
    default boolean add(T entity) throws Exception {
        if (entity == null || entity.getID() == null) {
            throw new NullPointerException("Entity is null");
        }

        if (XG7PluginsAPI.dbProcessor().exists(getPlugin(), getEntityClass(), entity.getID())) return true;

        try {
            Transaction.createTransaction(
                    getPlugin(),
                    entity,
                    Transaction.Type.INSERT
            ).process();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    default CompletableFuture<Boolean> addAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return add(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, XG7PluginsAPI.taskManager().getExecutor("database"));
    }

    /**
     * Retrieves an entity from the database by its ID asynchronously.
     *
     * @param id The identifier of the entity to retrieve
     * @return A CompletableFuture containing the retrieved entity
     */
    default T get(ID id) {
        if (id == null) return null;

        if (XG7PluginsAPI.database().containsCachedEntity(getPlugin(), id.toString()).join())
            return (T) XG7PluginsAPI.database().getCachedEntity(getPlugin(), id.toString()).join();

        try {
            return Query.selectFrom(getPlugin(), getEntityClass(), id).process().get(getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    default CompletableFuture<T> getAsync(ID id) {
        return CompletableFuture.supplyAsync(() -> get(id), XG7PluginsAPI.taskManager().getExecutor("database"));
    }

    default List<T> getAll() {
        try {
            return Query.selectAllFrom(getPlugin(), getEntityClass()).process().getList(getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(this::getAll, XG7PluginsAPI.taskManager().getExecutor("database"));
    }

    /**
     * Updates an existing entity in the database asynchronously.
     *
     * @param entity The entity to be updated
     * @return A CompletableFuture containing true if the operation was successful, false otherwise
     */
    default boolean update(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity is null");
        }

        try {
            Transaction.update(getPlugin(), entity).process();
            XG7PluginsAPI.database().cacheEntity(getPlugin(), entity.getID().toString(), entity);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    default CompletableFuture<Boolean> updateAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> update(entity), XG7PluginsAPI.taskManager().getExecutor("database"));
    }

    default boolean delete(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity is null");
        }

        try {
            Transaction.delete(getPlugin(), entity).process();
            XG7PluginsAPI.database().unCacheEntity(getPlugin(), entity.getID().toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    default CompletableFuture<Boolean> deleteAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> delete(entity), XG7PluginsAPI.taskManager().getExecutor("database"));
    }

    Plugin getPlugin();
    Class<T> getEntityClass();

}
