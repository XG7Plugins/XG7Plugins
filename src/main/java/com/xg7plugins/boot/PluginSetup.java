package com.xg7plugins.boot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginSetup {

    String prefix();

    String mainCommandName();
    String[] mainCommandAliases();

    String[] configs() default {};

    String[] onEnableDraw() default {};

    String[] reloadCauses() default {};


}
