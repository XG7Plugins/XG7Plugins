package com.xg7plugins.events;


import com.github.retrooper.packetevents.event.*;

public interface PacketListener extends Listener {

    default void onUserConnect(UserConnectEvent event) {
    }
    default void onUserLogin(UserLoginEvent event) {
    }
    default void onUserDisconnect(UserDisconnectEvent event) {
    }
    default void onPacketReceive(PacketReceiveEvent event) {
    }
    default void onPacketSend(PacketSendEvent event) {
    }
    default void onPacketEventExternal(PacketEvent event) {
    }

}
