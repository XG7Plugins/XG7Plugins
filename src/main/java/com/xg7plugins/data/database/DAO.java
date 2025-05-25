package com.xg7plugins.data.database;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Data Access Object (DAO) interface for handling database operations.
 * Provides asynchronous CRUD operations for entities.
 *
 * @param <ID> The type of the identifier used for the entity
 * @param <T>  The type of the entity being managed
 */
public interface DAO<ID, T> {

    /**
     * Adds a new entity to the database asynchronously.
     *
     * @param entity The entity to be added
     * @return A CompletableFuture containing true if the operation was successful, false otherwise
     * @throws ExecutionException If the computation threw an exception
     * @throws InterruptedException If the current thread was interrupted while waiting
     */
    CompletableFuture<Boolean> add(T entity) throws ExecutionException, InterruptedException;

    /**
     * Retrieves an entity from the database by its ID asynchronously.
     *
     * @param id The identifier of the entity to retrieve
     * @return A CompletableFuture containing the retrieved entity
     */
    CompletableFuture<T> get(ID id);

    /**
     * Updates an existing entity in the database asynchronously.
     *
     * @param entity The entity to be updated
     * @return A CompletableFuture containing true if the operation was successful, false otherwise
     */
    CompletableFuture<Boolean> update(T entity);

}
