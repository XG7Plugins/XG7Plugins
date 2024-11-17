package com.xg7plugins.commands.setup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Command {
    String name();
    String description();
    String syntax();
    String aliasesPath();
    String perm() default "";
    String[] enabledPath() default {"", "", "false"};
    boolean isOnlyInWorld() default false;
    boolean isOnlyPlayer() default false;
}
