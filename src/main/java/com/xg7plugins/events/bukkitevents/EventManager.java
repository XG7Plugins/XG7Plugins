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

public class EventManager implements Manager {

    private final HashMap<String, org.bukkit.event.Listener> listeners = new HashMap<>();

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

                Config config = plugin.getConfig(eventHandler.isEnabled().configName());

                boolean invert = eventHandler.isEnabled().invert();
                if (config != null) {
                    if (config.get(eventHandler.isEnabled().path(), Boolean.class).orElse(false) == invert) continue;
                }
                else if (invert) continue;

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
                                    if (!plugin.getEnabledWorlds().contains(playerEvent.getPlayer().getWorld().getName()))
                                        return;
                                }
                                if (event2 instanceof EntityEvent) {
                                    EntityEvent entityEvent = (EntityEvent) event2;
                                    if (!plugin.getEnabledWorlds().contains(entityEvent.getEntity().getWorld().getName()))
                                        return;
                                }
                                if (event2 instanceof WorldEvent) {
                                    WorldEvent worldEvent = (WorldEvent) event2;
                                    if (!plugin.getEnabledWorlds().contains(worldEvent.getWorld().getName()))
                                        return;
                                }
                                if (event2 instanceof BlockEvent) {
                                    BlockEvent blockEvent = (BlockEvent) event2;
                                    if (!plugin.getEnabledWorlds().contains(blockEvent.getBlock().getWorld().getName()))
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
    public void registerListeners(Plugin plugin, List<Listener> listeners) {
        registerListeners(plugin, listeners.toArray(new Listener[0]));
    }

    public void unregisterListeners(Plugin plugin) {
        HandlerList.unregisterAll(listeners.get(plugin.getName()));
        listeners.remove(plugin.getName());
    }

    public void reloadEvents(Plugin plugin) {
        unregisterListeners(plugin);
        registerListeners(plugin, plugin.loadEvents());
    }

}
