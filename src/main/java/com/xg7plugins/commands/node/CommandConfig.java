package com.xg7plugins.commands.node;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.config.utils.ConfigCheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for configuring command methods
 * Represents a part of the command node tree
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandConfig {

    String name() default "root";
    String parent() default "";
    int depth() default 1;

    XMaterial iconMaterial() default XMaterial.STONE;

    String syntax() default "";
    String permission() default "";
    String description() default "";

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

    /**
     * Configuration for enabling/disabling the command
     */
    ConfigCheck isEnabled() default @ConfigCheck(
            configName = "",
            path = ""
    );

}
