package com.xg7plugins.api.event.handlers;

import com.xg7plugins.api.event.EventPriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCancelled() default false;

}
