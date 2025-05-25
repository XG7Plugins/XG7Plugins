package com.xg7plugins.data.database.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark fields as database columns and specify their name and properties.
 * This annotation isn't required.
 *
 * @see Table
 * @see Pkey
 * @see FKey
 * @see Entity
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * Specifies the name of the database column.
     *
     * @return The column name in the database
     */
    String name();

    /**
     * Specifies the maximum length for the column.
     * A value of -1 indicates no specific length constraint.
     *
     * @return The maximum length of the column
     */
    int length() default -1;
}
