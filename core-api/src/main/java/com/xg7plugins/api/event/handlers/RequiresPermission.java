package com.xg7plugins.api.event.handlers;

public @interface RequiresPermission {

    String permission();
    boolean opBypass() default true;

    String messageValue() default "";

}
