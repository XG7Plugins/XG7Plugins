package com.xg7plugins.data.database.processor;

import com.xg7plugins.data.database.entity.Entity;

public class IllegalEntityException extends IllegalArgumentException {
    public IllegalEntityException(Class<? extends Entity> entity) {
        super(entity.getName() + " entity must have an empty constructor!");
    }
}
