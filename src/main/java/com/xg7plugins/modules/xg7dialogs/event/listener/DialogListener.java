package com.xg7plugins.modules.xg7dialogs.event.listener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.server.MinecraftServerVersion;

import java.util.Collections;
import java.util.Set;

@PacketListenerSetup
public class DialogListener implements PacketListener {
    @Override
    public boolean isEnabled() {
        return MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_21_6);
    }

    @Override
    public Set<PacketTypeCommon> getHandledEvents() {
        return Collections.singleton(PacketType.Play.Client.CUSTOM_CLICK_ACTION);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        WrapperPlayClientCustomClickAction clickAction = new WrapperPlayClientCustomClickAction(event);

        System.out.println("DIALOG " + clickAction.getPayload());

        PacketListener.super.onPacketReceive(event);
    }
}
