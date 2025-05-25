package com.xg7plugins.data.database.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark classes as database table entities and change their names.
 * This annotation is used at runtime to map Java classes to database tables.
 * This annotation isn't required
 *
 * @see Column
 * @see Pkey
 * @see FKey
 * @see Entity
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * Specifies the name of the database table.
     *
     * @return The name of the table in the database
     */
    String name();
}
