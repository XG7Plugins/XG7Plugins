package com.xg7plugins.data.database.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark a field as a primary key in a database entity.
 * <p>
 * When applied to a field in an entity class, this annotation indicates that
 * the field represents the primary key of the corresponding database table.
 * This information is used during table creation and database operations.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * public class User implements Entity<Long, User> {
 *    {@literal @}Pkey
 *     private Long id;
 *     // other fields
 * }
 * </pre>
 *
 * @see Entity
 * @see Table
 * @see Column
 * @see FKey
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Pkey {
}