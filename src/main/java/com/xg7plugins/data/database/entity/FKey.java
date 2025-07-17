package com.xg7plugins.data.database.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark fields as foreign keys in database entities.
 * <p>
 * When applied to a field in an entity class, this annotation indicates that
 * the field represents a foreign key relationship with another table.
 * This information is used during table creation and database operations.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * <code>
 * public class Order implements Entity<Long, Order> {
 *     {@literal @}FKey(origin_table = Product.class, origin_column = "id")
 *     private Long productId;
 *     // other fields
 * }
 * </code>
 * </pre>
 *
 * @see Entity
 * @see Table
 * @see Column
 * @see Pkey
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FKey {
    Class<? extends Entity> origin_table();

    String origin_column();
}