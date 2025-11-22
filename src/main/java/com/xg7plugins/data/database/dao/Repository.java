package com.xg7plugins.data.database.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for database operations.
 * Provides CRUD operations for entities.
 *
 * @param <ID> Type of identifier used for the entity
 * @param <T>  Type of entity being managed
 */
public interface Repository<ID, T extends Entity<?, ?>> {
    /**
     * Adds a new entity to the database asynchronously.
     * Checks if the entity already exists before inserting.
     *
     * @param entity The entity to be added
     * @return true if the operation was successful, false otherwise
     * @throws Exception If an error occurs during the operation
     */
    default boolean add(T entity) throws Exception {
        if (entity == null || entity.getID() == null) {
            throw new NullPointerException("Entity is null");
        }

        getPlugin().getDebug().info("database", "Adding new " + getEntityClass().getSimpleName() + " with entity id " + entity.getID() + "...");

        if (XG7Plugins.getAPI().dbProcessor().exists(getPlugin(), getEntityClass(), entity.getID())) return true;

        try {
            Transaction.createTransaction(
                    getPlugin(),
                    entity,
                    Transaction.Type.INSERT
            ).process();
            getPlugin().getDebug().info("database", "Added!");
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Async version of the add() method
     */
    default CompletableFuture<Boolean> addAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return add(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, XG7Plugins.getAPI().taskManager().getExecutor("database"));
    }

    /**
     * Gets an entity from the database by ID.
     * Checks cache first before querying the database.
     *
     * @param id The entity identifier
     * @return The found entity or null
     */
    default T get(ID id) {
        if (id == null) return null;

        getPlugin().getDebug().info("database", "Getting " + getEntityClass().getSimpleName() + " with id " + id + "...");

        if (XG7Plugins.getAPI().database().containsCachedEntity(getPlugin(), id.toString()).join())
            return (T) XG7Plugins.getAPI().database().getCachedEntity(getPlugin(), id.toString()).join();

        try {
            getPlugin().getDebug().info("database", "Querying database...");
            return Query.selectFrom(getPlugin(), getEntityClass(), id).process().get(getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Async version of get() method
     */
    default CompletableFuture<T> getAsync(ID id) {
        return CompletableFuture.supplyAsync(() -> get(id), XG7Plugins.getAPI().taskManager().getExecutor("database"));
    }

    /**
     * Returns all entities of type T from the database
     */
    default List<T> getAll() {
        try {
            getPlugin().getDebug().info("database", "Getting all " + getEntityClass().getSimpleName() + " from database..");
            return Query.selectAllFrom(getPlugin(), getEntityClass()).process().getList(getEntityClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Async version of the getAll() method
     */
    default CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(this::getAll, XG7Plugins.getAPI().taskManager().getExecutor("database"));
    }

    /**
     * Updates an existing entity in the database.
     * Also updates the cache after operation.
     *
     * @param entity The entity to be updated
     * @return true if the operation was successful
     */
    default boolean update(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity is null");
        }

        getPlugin().getDebug().info("database", "Updating " + getEntityClass().getSimpleName() + " with id " + entity.getID() + "...");

        try {
            Transaction.update(getPlugin(), entity).process();
            XG7Plugins.getAPI().database().cacheEntity(getPlugin(), entity.getID().toString(), entity);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Async version of the update() method
     */
    default CompletableFuture<Boolean> updateAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> update(entity), XG7Plugins.getAPI().taskManager().getExecutor("database"));
    }

    /**
     * Removes an entity from the database.
     * Also removes from the cache after operation.
     *
     * @param entity The entity to be removed
     * @return true if the operation was successful
     */
    default boolean delete(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity is null");
        }

        getPlugin().getDebug().info("database", "Deleting " + getEntityClass().getSimpleName() + " with id " + entity.getID() + "...");

        try {
            Transaction.delete(getPlugin(), entity).process();
            XG7Plugins.getAPI().database().unCacheEntity(getPlugin(), entity.getID().toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Async version of the delete() method
     */
    default CompletableFuture<Boolean> deleteAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> delete(entity), XG7Plugins.getAPI().taskManager().getExecutor("database"));
    }

    Plugin getPlugin();

    Class<T> getEntityClass();

}