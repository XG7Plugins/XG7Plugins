package com.xg7plugins.events.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.*;
import com.xg7plugins.boot.Plugin;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;


import java.util.*;

/**
 * Manages packet event listeners for plugins, handling registration, unregistration,
 * and reloading of packet event handlers. This manager integrates with PacketEvents API
 * to process network packets and related events.
 */
public class PacketEventManager {

    private final HashMap<String, List<PacketListenerCommon>> packetListeners = new HashMap<>();

    /**
     * Registers multiple packet listeners for a specific plugin.
     * Each listener must have a PacketEventHandler annotation to be valid.
     *
     * @param plugin    The plugin registering the listeners
     * @param listeners Array of PacketListener instances to register
     * @throws IllegalArgumentException if a listener lacks the PacketEventHandler annotation
     */
    public void registerListeners(Plugin plugin, com.xg7plugins.events.PacketListener... listeners) {

        if (listeners == null) return;

        packetListeners.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (com.xg7plugins.events.PacketListener listener : listeners) {

            if (listener == null) continue;

            if (!listener.isEnabled()) continue;

            PacketListenerSetup packetEventHandler = listener.getClass().getAnnotation(PacketListenerSetup.class);

            if (packetEventHandler == null) throw new IllegalArgumentException("PacketListener must have PacketEventHandler annotation");

            com.github.retrooper.packetevents.event.PacketListener packetListener = new com.github.retrooper.packetevents.event.PacketListener() {
                @Override
                public void onUserConnect(UserConnectEvent event) {
                    listener.onUserConnect(event);
                }

                @Override
                public void onUserLogin(UserLoginEvent event) {
                    listener.onUserLogin(event);
                }

                @Override
                public void onUserDisconnect(UserDisconnectEvent event) {
                    listener.onUserDisconnect(event);
                }

                @Override
                public void onPacketReceive(PacketReceiveEvent event) {
                    Set<PacketTypeCommon> packetTypes = listener.getHandledEvents();

                    if (packetTypes.isEmpty()) {
                        listener.onPacketReceive(event);
                        return;
                    }

                    if (packetTypes.stream().anyMatch(type -> type == event.getPacketType())) {
                        listener.onPacketReceive(event);
                    }
                }

                @Override
                public void onPacketSend(PacketSendEvent event) {
                    Set<PacketTypeCommon> packetTypes = listener.getHandledEvents();

                    if (packetTypes.isEmpty()) {
                        listener.onPacketSend(event);
                        return;
                    }

                    if (packetTypes.stream().anyMatch(type -> type == event.getPacketType())) {
                        listener.onPacketSend(event);
                    }
                }

                @Override
                public void onPacketEventExternal(PacketEvent event) {
                    listener.onPacketEventExternal(event);
                }
            };

            PacketListenerCommon common = PacketEvents.getAPI().getEventManager().registerListener(packetListener, packetEventHandler.priority());

            packetListeners.get(plugin.getName()).add(common);

        }

    }

    /**
     * Registers a list of packet listeners for a specific plugin.
     * Converts the list to an array and delegates to the array-based registration method.
     *
     * @param plugin    The plugin registering the listeners
     * @param listeners List of PacketListener instances to register
     */
    public void registerListeners(Plugin plugin, List<com.xg7plugins.events.PacketListener> listeners) {
        if (listeners == null) return;
        registerListeners(plugin, listeners.toArray(new com.xg7plugins.events.PacketListener[0]));
    }

    /**
     * Unregisters all packet listeners associated with a specific plugin.
     * Clears the listener collection for the plugin after unregistering.
     *
     * @param plugin The plugin whose listeners should be unregistered
     */
    public void unregisterListeners(Plugin plugin) {
        if (!packetListeners.containsKey(plugin.getName())) return;
        for (PacketListenerCommon listener : packetListeners.get(plugin.getName())) PacketEvents.getAPI().getEventManager().unregisterListener(listener);

        packetListeners.get(plugin.getName()).clear();
    }

    /**
     * Reloads all packet listeners for a specific plugin.
     * Unregisters existing listeners and registers new ones from the plugin.
     *
     * @param plugin The plugin whose listeners should be reloaded
     */
    public void reloadListeners(Plugin plugin) {
        unregisterListeners(plugin);
        registerListeners(plugin, plugin.loadPacketEvents());
    }


}
