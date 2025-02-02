package com.xg7plugins.data.database.entity;

public interface Entity<T extends Entity<T>> {

    boolean equals(T other);

}
