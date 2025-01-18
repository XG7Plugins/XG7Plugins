package com.xg7plugins.events;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    boolean isOnlyInWorld() default false;
    EventPriority priority() default EventPriority.NORMAL;
    String[] enabledPath() default {"", "", "false"};

}
