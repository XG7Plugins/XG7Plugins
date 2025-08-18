package com.xg7plugins.data.config.section;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {

    String name();

    String defaultValue() default "";

}
