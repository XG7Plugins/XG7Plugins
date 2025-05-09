package com.xg7plugins.commands.setup;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.ConfigBoolean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CommandSetup {

    String name();
    String description();
    String syntax();
    String permission() default "";
    Class<? extends Plugin> pluginClass();

    ConfigBoolean isEnabled() default @ConfigBoolean(
            configName = "",
            path = ""
    );
    boolean isAsync() default false;
    boolean isPlayerOnly() default false;
    boolean isConsoleOnly() default false;
    boolean isInEnabledWorldOnly() default false;

}
