package com.xg7plugins.data.database;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DAO<ID,T> {

    CompletableFuture<Boolean> add(T entity) throws ExecutionException, InterruptedException;
    CompletableFuture<T> get(ID id);
    CompletableFuture<Boolean> update(T entity);

}
