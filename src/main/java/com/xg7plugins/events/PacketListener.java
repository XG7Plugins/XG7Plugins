package com.xg7plugins.events;

import com.github.retrooper.packetevents.event.PacketListenerPriority;

public interface PacketListener extends Listener, com.github.retrooper.packetevents.event.PacketListener {
    default PacketListenerPriority getPriority() {
        return PacketListenerPriority.NORMAL;
    }
}
