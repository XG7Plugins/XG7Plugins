package com.xg7plugins.events.bukkitevents;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.managers.Manager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Manages the registration and handling of Bukkit events for plugins.
 * This class provides functionality to register, unregister, and reload event listeners
 * while supporting world-specific event handling and conditional event enabling.
 */
public class EventManager implements Manager {

    private final HashMap<String, org.bukkit.event.Listener> listeners = new HashMap<>();

    /**
     * Registers multiple event listeners for a specific plugin.
     * Handles packet listeners, enabled/disabled events, and world-specific event filtering.
     *
     * @param plugin The plugin registering the listeners
     * @param events Array of listeners to register
     */
    public void registerListeners(Plugin plugin, Listener... events) {

        if (events == null) return;

        listeners.put(plugin.getName(), new org.bukkit.event.Listener() {});

        for (Listener event : events) {
            if (event == null) continue;

            if (event.getClass().isAssignableFrom(PacketListener.class)) continue;

            if (!event.isEnabled()) continue;
            for (Method method : event.getClass().getMethods()) {
                if (!method.isAnnotationPresent(EventHandler.class)) continue;
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);

                if (!eventHandler.isEnabled().configName().isEmpty()) {
                    Config config = Config.of(eventHandler.isEnabled().configName(), plugin);

                    boolean invert = eventHandler.isEnabled().invert();
                    if (config != null) {
                        if (config.get(eventHandler.isEnabled().path(), Boolean.class).orElse(false) == invert) continue;
                    }
                    else if (invert) continue;
                }

                plugin.getServer().getPluginManager().registerEvent(
                        (Class<? extends org.bukkit.event.Event>) method.getParameterTypes()[0],
                        listeners.get(plugin.getName()),
                        eventHandler.priority(),
                        (listener, event2) -> {
                            if (eventHandler.ignoreCancelled() && ((event2 instanceof Cancellable) && ((Cancellable)event2).isCancelled())) return;
                            if (event2.getClass() != method.getParameterTypes()[0]) return;
                            if (eventHandler.isOnlyInWorld()) {
                                if (event2 instanceof PlayerEvent) {
                                    PlayerEvent playerEvent = (PlayerEvent) event2;
                                    if (!XG7PluginsAPI.isInWorldEnabled(plugin, playerEvent.getPlayer()))
                                        return;
                                }
                                if (event2 instanceof EntityEvent) {
                                    EntityEvent entityEvent = (EntityEvent) event2;
                                    if (!XG7PluginsAPI.isWorldEnabled(plugin, entityEvent.getEntity().getWorld()))
                                        return;
                                }
                                if (event2 instanceof WorldEvent) {
                                    WorldEvent worldEvent = (WorldEvent) event2;
                                    if (!XG7PluginsAPI.isWorldEnabled(plugin, worldEvent.getWorld()))
                                        return;
                                }
                                if (event2 instanceof BlockEvent) {
                                    BlockEvent blockEvent = (BlockEvent) event2;
                                    if (!XG7PluginsAPI.isWorldEnabled(plugin, blockEvent.getBlock().getWorld()))
                                        return;
                                }
                            }

                            try {
                                method.invoke(event, event2);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        plugin
                );
            }


        }

    }

    /**
     * Registers a list of event listeners for a specific plugin.
     * Converts the list to array and delegates to the main registration method.
     *
     * @param plugin    The plugin registering the listeners
     * @param listeners List of listeners to register
     */
    public void registerListeners(Plugin plugin, List<Listener> listeners) {
        if (listeners == null) return;
        registerListeners(plugin, listeners.toArray(new Listener[0]));
    }

    /**
     * Unregisters all event listeners associated with a specific plugin.
     * Cleans up the listeners map after unregistration.
     *
     * @param plugin The plugin whose listeners should be unregistered
     */
    public void unregisterListeners(Plugin plugin) {
        HandlerList.unregisterAll(listeners.get(plugin.getName()));
        listeners.remove(plugin.getName());
    }

    /**
     * Reloads all event listeners for a specific plugin.
     * Unregisters existing listeners and registers them again.
     *
     * @param plugin The plugin whose events should be reloaded
     */
    public void reloadEvents(Plugin plugin) {
        unregisterListeners(plugin);
        registerListeners(plugin, plugin.loadEvents());
    }

}
