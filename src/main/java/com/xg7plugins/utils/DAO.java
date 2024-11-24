package com.xg7plugins.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.xg7plugins.Plugin;
import com.xg7plugins.data.database.Entity;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public abstract class DAO<ID,T extends Entity> {

    public abstract void add(T entity) throws ExecutionException, InterruptedException;
    public abstract CompletableFuture<T> get(ID id);
    public abstract CompletableFuture<Void> update(T entity);

}
