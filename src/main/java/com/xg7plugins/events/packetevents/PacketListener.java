package com.xg7plugins.events.packetevents;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;

import java.util.List;

/**
 * Interface for handling various packet-related events in the system.
 * Provides default implementations for packet event handling methods.
 */
public interface PacketListener {

    boolean isEnabled();

    default ListenerPriority getPriority() {
        return ListenerPriority.NORMAL;
    }

    List<PacketType> getPacketTypes();

    default void onPacketSending(PacketEvent event) {

    }

    default void onPacketReceiving(PacketEvent event) {

    }



}
