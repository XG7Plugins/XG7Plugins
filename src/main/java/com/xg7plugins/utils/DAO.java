package com.xg7plugins.utils;

import com.xg7plugins.data.database.entity.Entity;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DAO<ID,T extends Entity> {

    CompletableFuture<Void> add(T entity) throws ExecutionException, InterruptedException;
    CompletableFuture<T> get(ID id);
    CompletableFuture<Void> update(T entity);

}
