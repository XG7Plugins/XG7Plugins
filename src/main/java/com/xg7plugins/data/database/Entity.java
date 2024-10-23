package com.xg7plugins.data.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Entity {

    @Retention(RetentionPolicy.RUNTIME)
    @interface PKey {
        boolean autoincrement() default false;
    }
    @Retention(RetentionPolicy.RUNTIME)
    @interface FKey {
        String reference();
        String table();
    }
}
