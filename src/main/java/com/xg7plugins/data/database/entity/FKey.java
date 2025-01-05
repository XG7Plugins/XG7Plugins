package com.xg7plugins.data.database.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FKey {
    Class<? extends Entity> origin_table();
    String origin_column();
}
