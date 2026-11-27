package com.xg7plugins.api.event;

public interface Listener {
    default boolean shouldRegister() {
        return true;
    }
}
