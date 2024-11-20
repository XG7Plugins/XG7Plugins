package com.xg7plugins.events.bukkitevents;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.utils.reflection.ReflectionClass;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EventManager {

    private final HashMap<String, Listener> listeners = new HashMap<>();

    public void registerPlugin(Plugin plugin, Class<? extends Event>... eventClasses) {

        plugin.getLog().info("Loading Events...");

        if (eventClasses == null) return;

        listeners.put(plugin.getName(), new Listener() {});

        for (Class<?> eventClass : eventClasses) {

            if (eventClass.isAssignableFrom(PacketEvent.class)) continue;

            Event event = (Event) ReflectionClass.of(eventClass).newInstance().getObject();

            if (!event.isEnabled()) continue;
            for (Method method : event.getClass().getMethods()) {
                if (!method.isAnnotationPresent(EventHandler.class)) continue;
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);

                Config config = plugin.getConfigsManager().getConfig(eventHandler.enabledPath()[0]);

                boolean invert = Boolean.parseBoolean(eventHandler.enabledPath()[2]);
                if (config != null) if ((boolean) config.get(eventHandler.enabledPath()[1]) == invert) continue;
                else if (invert) continue;

                plugin.getServer().getPluginManager().registerEvent(
                        (Class<? extends org.bukkit.event.Event>) method.getParameterTypes()[0],
                        listeners.get(plugin.getName()),
                        eventHandler.priority(),
                        (listener, event2) -> {
                            if (eventHandler.isOnlyInWorld()) {
                                if (event2 instanceof PlayerEvent) {
                                    PlayerEvent playerEvent = (PlayerEvent) event2;
                                    if (!plugin.getEnabledWorlds().contains(playerEvent.getPlayer().getWorld().getName()))
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

                            if (method.getParameterTypes().length == 2) {
                                try {
                                    method.invoke(event, event2, plugin);
                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                    throw new RuntimeException(ex);
                                }
                                return;
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

        plugin.getLog().fine("Events loaded.");
    }

    public void unregisterEvents(Plugin plugin) {
        HandlerList.unregisterAll(listeners.get(plugin.getName()));
        listeners.remove(plugin.getName());
    }

}
