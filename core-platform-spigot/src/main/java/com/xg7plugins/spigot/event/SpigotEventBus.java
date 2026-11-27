package com.xg7plugins.spigot.event;

import com.sun.istack.internal.NotNull;
import com.xg7plugins.api.event.*;
import com.xg7plugins.api.event.handlers.EventHandler;
import com.xg7plugins.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotEventBus implements EventBus {

    private final Map<Plugin, Set<Listener>> registeredListeners = new ConcurrentHashMap<>();
    private final Map<Plugin, org.bukkit.event.Listener> bukkitHandlers = new ConcurrentHashMap<>();
    private final Set<EventCondition> eventConditions = new HashSet<>();

    @Override
    public Map<Plugin, Set<Listener>> getRegisteredListeners() {
        return registeredListeners;
    }

    @Override
    public void registerListener(@NotNull Plugin plugin, @NotNull Listener listener) {
        if (!listener.shouldRegister() || !eventConditions.stream().allMatch(cond -> cond.shouldRegister(plugin, listener))) {
            plugin.getLogger().info("events", "Skipped listener §e" + listener.getClass().getSimpleName() + " §7(condition/disabled).");
            return;
        }

        registeredListeners.computeIfAbsent(plugin, p -> ConcurrentHashMap.newKeySet()).add(listener);
        org.bukkit.event.Listener bukkitListener = bukkitHandlers.computeIfAbsent(plugin, p -> new org.bukkit.event.Listener() {});

        int registeredCount = 0;
        int skippedMethods = 0;

        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;

            if (!eventConditions.stream().allMatch(cond -> cond.shouldRegisterMethod(plugin, listener, method))) {
                skippedMethods++;
                continue;
            }

            EventHandler meta = method.getAnnotation(EventHandler.class);

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            Bukkit.getPluginManager().registerEvent(
                    eventClass,
                    bukkitListener,
                    meta.priority().toBukkit(),
                    (ignored, event) -> invokeListenerMethod(plugin, listener, method, event),
                    plugin.getHandle(JavaPlugin.class),
                    meta.ignoreCancelled()
            );
            registeredCount++;
        }

        plugin.getLogger().info("events", "Registered §b" + listener.getClass().getSimpleName()
                + " §8(§a" + registeredCount + " handlers§8"
                + (skippedMethods > 0 ? ", §e" + skippedMethods + " skipped§8" : "") + ")");
    }

    @Override
    public void invokeListenerMethod(Plugin plugin, Listener listener, Method method, Object event) {
        Set<Listener> active = registeredListeners.get(plugin);
        if (active == null || !active.contains(listener)) return;
        if (!method.getParameterTypes()[0].isAssignableFrom(event.getClass())) return;

        for (EventCondition condition : eventConditions) {
            if (!condition.canExecute(plugin, listener, method, event)) return;
        }

        try {
            method.invoke(listener, event);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe(
                    "Error executing §e" + listener.getClass().getSimpleName() + "#" + method.getName()
                            + " §7for event §b" + event.getClass().getSimpleName(), e
            );
            throw new RuntimeException("Error invoking event method " + method.getName(), e);
        }
    }

    @Override
    public void unregisterListener(Plugin plugin, Listener listener) {
        if (registeredListeners.containsKey(plugin)) registeredListeners.get(plugin).remove(listener);
    }

    @Override
    public void unregisterAllListeners(Plugin plugin) {
        if (!registeredListeners.containsKey(plugin)) return;

        registeredListeners.remove(plugin);
        HandlerList.unregisterAll(plugin.getHandle(JavaPlugin.class));
    }

    @Override
    public Set<EventCondition> getEventConditions() {
        return eventConditions;
    }

    @Override
    public void registerEventCondition(EventCondition eventCondition) {
        this.eventConditions.add(eventCondition);
    }
}
