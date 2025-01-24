package com.xg7plugins.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.boot.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketEventManager {

    private final HashMap<String, List<PacketListener>> listeners = new HashMap<>();

    public void registerPlugin(Plugin plugin, PacketListener... events) {

        plugin.getLog().loading("Loading Packet Events...");

        if (events == null) return;

        listeners.put(plugin.getName(), new ArrayList<>());
        for (PacketListener event : events) {
            if (event == null) continue;

            if (!event.isEnabled()) continue;
            PacketEvents.getAPI().getEventManager().registerListener(event, event.getPriority());
            listeners.get(plugin.getName()).add(event);
        }

        plugin.getLog().loading("Packet Events loaded.");
    }

    public void unregisterEvents(Plugin plugin) {
        if (!listeners.containsKey(plugin.getName())) return;
        for (PacketListener listener : listeners.get(plugin.getName())) {
            PacketEvents.getAPI().getEventManager().unregisterListener(listener.asAbstract(listener.getPriority()));
        }
        listeners.remove(plugin.getName());
    }
}
