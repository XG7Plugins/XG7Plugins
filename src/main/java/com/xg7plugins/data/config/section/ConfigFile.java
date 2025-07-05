package com.xg7plugins.data.config.section;

import com.xg7plugins.boot.Plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigFile {

    Class<? extends Plugin> plugin();
    String configName();
    String path() default "";

}
