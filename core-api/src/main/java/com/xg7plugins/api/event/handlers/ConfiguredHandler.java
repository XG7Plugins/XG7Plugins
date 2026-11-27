package com.xg7plugins.api.event.handlers;

public @interface ConfiguredHandler {

    boolean runOnlyOnEnabledWorlds() default false;
    boolean runOnlyOnEnabledRegions() default false;

    String configName() default "";
    String enabledPath() default "";


}

