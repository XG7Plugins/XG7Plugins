package com.xg7plugins.events.packetevents;

import com.xg7plugins.events.Event;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import io.netty.channel.*;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class PacketEventManager extends PacketManagerBase {

    @SneakyThrows
    public void create(Player player) {

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet)
                    throws Exception {

                Object modPacket = packet;

                for (List<Event> eventList : events.values()) {
                    for (Event event : eventList) {
                        for (Method method : event.getClass().getMethods()) {
                            if (!method.isAnnotationPresent(PacketEventHandler.class)) continue;
                            PacketEventHandler eventHandler = method.getAnnotation(PacketEventHandler.class);
                            if (packet.getClass().getName().endsWith(eventHandler.packet()))
                                modPacket = method.invoke(event, player, ReflectionObject.of(packet));
                        }
                    }
                }
                super.channelRead(context, modPacket);
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {

                Object modPacket = packet;

                for (List<Event> eventList : events.values()) {
                    for (Event event : eventList) {
                        for (Method method : event.getClass().getMethods()) {
                            if (!method.isAnnotationPresent(PacketEventHandler.class)) continue;
                            PacketEventHandler eventHandler = method.getAnnotation(PacketEventHandler.class);
                            if (packet.getClass().getName().endsWith(eventHandler.packet()))
                                modPacket = method.invoke(event, player, ReflectionObject.of(packet));
                        }
                    }
                }

                super.write(context, modPacket, channelPromise);
            }
        };

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        Channel channel = NMSUtil.getValueByFieldName(playerNMS.getNetworkManager().getObject(), "Channel");

        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    @Override
    public void stopEvent(Player player) {
        try {
            PlayerNMS playerNMS = PlayerNMS.cast(player);
            Channel channel = NMSUtil.getValueByFieldName(playerNMS.getNetworkManager().getObject(), "Channel");
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
