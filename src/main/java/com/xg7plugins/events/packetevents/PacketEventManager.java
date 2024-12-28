package com.xg7plugins.events.packetevents;

import com.xg7plugins.events.Listener;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.nms.Packet;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import io.netty.channel.*;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public class PacketEventManager extends PacketManagerBase {

    @SneakyThrows
    public void create(Player player) {

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object o)
                    throws Exception {

                Packet packet = new Packet(o);

                processPacket(packet, player);

                super.channelRead(context, packet.getPacket());
            }

            @Override
            public void write(ChannelHandlerContext context, Object o, ChannelPromise channelPromise) throws Exception {

                Packet packet = new Packet(o);

                processPacket(packet, player);

                super.write(context, packet.getPacket(), channelPromise);
            }
        };

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        Channel channel = NMSUtil.getValueByFieldName(playerNMS.getNetworkManager().getObject(), "Channel");

        ChannelPipeline channelPipeline = channel.pipeline();
        if (!channelPipeline.names().contains(player.getName())) {
            channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        }
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
