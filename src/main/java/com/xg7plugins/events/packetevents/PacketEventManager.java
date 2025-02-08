package com.xg7plugins.events.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.PacketListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketEventManager {

    private HashMap<String, List<PacketListenerCommon>> packetListeners = new HashMap<>();

    public void registerListeners(Plugin plugin, PacketListener... listeners) {

        packetListeners.putIfAbsent(plugin.getName(), new ArrayList<>());

        for (PacketListener listener : listeners) {

            if (!listener.isEnabled()) continue;

            PacketEventHandler packetEventHandler = listener.getClass().getAnnotation(PacketEventHandler.class);

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
                    if (packetEventHandler.packet() == PacketEventType.ALL) {
                        listener.onPacketReceive(event);
                        return;
                    }
                    if (packetEventHandler.packet() == PacketEventType.CLIENT_ALL  && event.getPacketType().getSide() == PacketSide.CLIENT) {
                        if (event.getPacketId() < 0) {
                            listener.onPacketReceive(event);
                        }
                        return;
                    }

                    String packetType = packetEventHandler.packet().name();

                    String packetName = packetType.substring(0, packetType.indexOf("_")).replace("CLIENT_", "");

                    if (packetName.equals(event.getPacketType().getName())) listener.onPacketReceive(event);

                }

                @Override
                public void onPacketSend(PacketSendEvent event) {
                    if (packetEventHandler.packet() == PacketEventType.ALL) {
                        listener.onPacketSend(event);
                        return;
                    }
                    if (packetEventHandler.packet() == PacketEventType.SERVER_ALL) {
                        if (event.getPacketId() < 0) listener.onPacketSend(event);
                        return;
                    }

                    String packetType = packetEventHandler.packet().name();

                    String packetName = packetType.substring(0, packetType.indexOf("_")).replace("SERVER_", "");

                    if (packetName.equals(event.getPacketType().getName())) listener.onPacketSend(event);
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

    public void registerListeners(Plugin plugin, List<PacketListener> listeners) {
        registerListeners(plugin, listeners.toArray(new PacketListener[0]));
    }

    public void unregisterListeners(Plugin plugin) {
        for (PacketListenerCommon listener : packetListeners.get(plugin.getName())) PacketEvents.getAPI().getEventManager().unregisterListener(listener);

        packetListeners.remove(plugin.getName());
    }


}
