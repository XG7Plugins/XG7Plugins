package com.xg7plugins.data.database.entity;

import com.xg7plugins.data.dao.DAO;

/**
 * Represents a generic database entity with a unique identifier.
 * This interface provides basic functionality required for database entities,
 * including identity comparison and ID retrieval.
 *
 * @param <ID> The type of the entity's identifier
 * @param <T>  The implementing entity type
 * @see com.xg7plugins.data.database.entity.Entity
 */
public interface Entity<ID, T extends Entity<ID, T>> {

    /**
     * Compares this entity with another entity for equality.
     *
     * @param other The entity to compare with
     * @return true if the entities are equal, false otherwise
     */
    boolean equals(T other);

    /**
     * Retrieves the unique identifier of this entity.
     *
     * @return The entity's identifier
     */
    ID getID();

}
