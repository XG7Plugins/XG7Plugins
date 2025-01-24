package com.xg7plugins.events.packetevents;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.utils.reflection.nms.Packet;
import com.xg7plugins.utils.reflection.nms.PacketEvent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class PacketEventManagerBase {

    protected final HashMap<String, List<Listener>> events = new HashMap<>();


    public void registerPlugin(Plugin plugin, PacketListener... events) {

        plugin.getLog().loading("Loading Packet Events...");

        if (events == null) return;

        this.events.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (PacketListener event : events) {
            if (event == null) continue;
            if (!event.isEnabled()) continue;
            this.events.get(plugin.getName()).add(event);
        }
        plugin.getLog().loading("Successfully loaded Packet Events for " + plugin.getName() +"!");
    }

    protected void processPacket(Packet packet, Player player) throws InvocationTargetException, IllegalAccessException {
        PacketEvent packetEvent = new PacketEvent(player, packet);

        for (List<Listener> eventList : events.values()) {
            for (Listener event : eventList) {
                for (Method method : event.getClass().getMethods()) {
                    if (!method.isAnnotationPresent(PacketEventHandler.class)) continue;
                    PacketEventHandler eventHandler = method.getAnnotation(PacketEventHandler.class);
                    if (packet.getPacketClass().getPacketName().endsWith(eventHandler.packet()))
                        method.invoke(event, packetEvent);
                }
            }
        }
    }

    public abstract void stopEvent(Player player);

    public void unregisterPlugin(Plugin plugin) {
        events.remove(plugin.getName());
    }

    public abstract void create(Player player);

}
