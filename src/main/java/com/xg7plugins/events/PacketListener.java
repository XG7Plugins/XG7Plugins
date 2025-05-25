package com.xg7plugins.events;


import com.github.retrooper.packetevents.event.*;

/**
 * Interface for handling various packet-related events in the system.
 * Provides default implementations for packet event handling methods.
 */
public interface PacketListener extends Listener {

    /**
     * Called when a user attempts to connect to the server.
     *
     * @param event The connection event containing user details
     */
    default void onUserConnect(UserConnectEvent event) {
    }

    /**
     * Called when a user successfully logs into the server.
     *
     * @param event The login event containing user details
     */
    default void onUserLogin(UserLoginEvent event) {
    }

    /**
     * Called when a user disconnects from the server.
     *
     * @param event The disconnect event containing user details
     */
    default void onUserDisconnect(UserDisconnectEvent event) {
    }

    /**
     * Called when a packet is received from a client.
     *
     * @param event The packet receives an event containing packet data
     */
    default void onPacketReceive(PacketReceiveEvent event) {
    }

    /**
     * Called when a packet is sent to a client.
     *
     * @param event The packet send event containing packet data
     */
    default void onPacketSend(PacketSendEvent event) {
    }

    /**
     * Called for external packet events that don't fit other categories.
     *
     * @param event The generic packet event
     */
    default void onPacketEventExternal(PacketEvent event) {
    }

}
