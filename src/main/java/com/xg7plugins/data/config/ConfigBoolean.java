package com.xg7plugins.data.config;

public @interface ConfigBoolean {

    String configName();
    String path();
    boolean invert() default false;

}
