package com.xg7plugins.api.event;

import com.xg7plugins.api.plugin.Plugin;

import java.lang.reflect.Method;

public interface EventCondition {

    default boolean shouldRegister(Plugin plugin, Listener listener) {
        return true;
    }

    default boolean shouldRegisterMethod(Plugin plugin, Listener listener, Method method) {
        return true;
    }

    default boolean canExecute(Plugin plugin, Listener listener, Method method, Object nativeEvent) {
        return true;
    }

}
