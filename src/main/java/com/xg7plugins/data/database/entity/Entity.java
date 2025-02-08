package com.xg7plugins.data.database.entity;

public interface Entity<ID, T extends Entity<ID, T>> {

    boolean equals(T other);

    ID getID();

}
