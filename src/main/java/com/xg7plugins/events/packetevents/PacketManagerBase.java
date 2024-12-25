package com.xg7plugins.events.packetevents;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class PacketManagerBase {

    protected final HashMap<String, List<Event>> events = new HashMap<>();


    public void registerPlugin(Plugin plugin, PacketEvent... events) {

        plugin.getLog().loading("Loading Packet Events...");

        if (events == null) return;

        this.events.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (PacketEvent event : events) {
            if (event == null) continue;
            if (!event.isEnabled()) continue;
            this.events.get(plugin.getName()).add(event);
        }
        plugin.getLog().loading("Successfully loaded Packet Events!");
    }

    public abstract void stopEvent(Player player);

    public void unregisterPlugin(Plugin plugin) {
        events.remove(plugin.getName());
    }

    public abstract void create(Player player);

}
