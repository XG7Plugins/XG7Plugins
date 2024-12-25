package com.xg7plugins.utils;

import com.xg7plugins.data.database.Entity;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public abstract class DAO<ID,T extends Entity> {

    public abstract CompletableFuture<Void> add(T entity) throws ExecutionException, InterruptedException;
    public abstract CompletableFuture<T> get(ID id);
    public abstract CompletableFuture<Void> update(T entity);

}
