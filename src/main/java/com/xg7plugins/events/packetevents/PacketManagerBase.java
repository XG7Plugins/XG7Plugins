package com.xg7plugins.events.packetevents;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.utils.reflection.ReflectionClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class PacketManagerBase {

    protected final HashMap<String, List<Event>> events = new HashMap<>();


    public void registerPlugin(Plugin plugin) {

        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());

        Set<Class<? extends PacketEvent>> events = reflections.getSubTypesOf(PacketEvent.class);

        if (events.isEmpty()) return;

        this.events.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (Class<? extends PacketEvent> eventClass : events) {
            PacketEvent packetEvent = (PacketEvent) ReflectionClass.of(eventClass).newInstance().getObject();
            if (!packetEvent.isEnabled()) continue;
            this.events.get(plugin.getName()).add(packetEvent);
        }
    }

    public abstract void stopEvent(Player player);

    public void unregisterPlugin(Plugin plugin) {
        events.remove(plugin.getName());
    }

    public abstract void create(Player player);

}
