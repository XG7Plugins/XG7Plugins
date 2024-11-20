package com.xg7plugins.events.packetevents;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.utils.reflection.ReflectionClass;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class PacketManagerBase {

    protected final HashMap<String, List<Event>> events = new HashMap<>();


    public void registerPlugin(Plugin plugin, Class<? extends PacketEvent>... eventClasses) {

        plugin.getLog().loading("Loading Packet Events...");

        if (eventClasses == null) return;

        this.events.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (Class<? extends PacketEvent> eventClass : eventClasses) {
            PacketEvent packetEvent = (PacketEvent) ReflectionClass.of(eventClass).newInstance().getObject();
            if (!packetEvent.isEnabled()) continue;
            this.events.get(plugin.getName()).add(packetEvent);
        }
        plugin.getLog().loading("Successfully loaded Packet Events!");
    }

    public abstract void stopEvent(Player player);

    public void unregisterPlugin(Plugin plugin) {
        events.remove(plugin.getName());
    }

    public abstract void create(Player player);

}
