package com.xg7plugins.api.event;

import com.xg7plugins.api.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public interface EventBus {

    Map<Plugin, Set<Listener>> getRegisteredListeners();

    default void registerListeners(Plugin plugin, Listener... listeners) {
        Arrays.asList(listeners).forEach(l -> registerListener(plugin, l));
    }
    default void registerPluginListeners(Plugin plugin) {
        plugin.loadEvents().forEach(l -> registerListener(plugin, l));
    }

    void registerListener(Plugin plugin, Listener listener);
    void invokeListenerMethod(Plugin plugin, Listener listener, Method method, Object event);

    void unregisterListener(Plugin plugin, Listener listener);
    void unregisterAllListeners(Plugin plugin);

    Set<EventCondition> getEventConditions();

    void registerEventCondition(EventCondition eventCondition);

}
