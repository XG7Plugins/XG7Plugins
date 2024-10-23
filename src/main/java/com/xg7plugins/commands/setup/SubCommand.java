package com.xg7plugins.commands.setup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SubCommand {
    String syntax();
    String name() default "";
    String description();
    String perm() default "";
    boolean isOnlyInWorld() default false;
    boolean isOnlyPlayer() default false;
    SubCommandType type();
}
