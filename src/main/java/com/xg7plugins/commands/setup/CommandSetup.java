package com.xg7plugins.commands.setup;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.utils.ConfigCheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for setting up command configurations.
 * This annotation is used to define command properties and execution constraints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CommandSetup {

    /**
     * The name of the command
     */
    String name();

    /**
     * A description of what the command does
     */
    String description();

    /**
     * The syntax/usage pattern of the command
     */
    String syntax();

    /**
     * The permission required to use the command
     */
    String permission() default "";

    /**
     * The plugin class that owns this command
     */
    Class<? extends Plugin> pluginClass();

    /**
     * Configuration for enabling/disabling the command
     */
    ConfigCheck isEnabled() default @ConfigCheck(
            configName = "",
            path = ""
    );

    /**
     * Whether the command should be executed asynchronously
     */
    boolean isAsync() default false;

    /**
     * Whether the command can only be executed by players
     */
    boolean isPlayerOnly() default false;

    /**
     * Whether the command can only be executed by console
     */
    boolean isConsoleOnly() default false;

    /**
     * Whether the command can only be executed in enabled worlds
     */
    boolean isInEnabledWorldOnly() default false;

}
