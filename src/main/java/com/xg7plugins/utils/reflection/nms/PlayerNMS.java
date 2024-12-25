package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;


@Getter
@AllArgsConstructor
public class PlayerNMS {

    private final Player player;
    private final ReflectionObject craftPlayerHandle;
    private final ReflectionObject playerConnection;
    private final ReflectionObject networkManager;

    @SneakyThrows
    public static PlayerNMS cast(Player player) {

        ReflectionObject craftPlayer = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player);

        ReflectionObject handle = craftPlayer.getMethod("getHandle").invokeToRObject();

        if (XG7Plugins.getMinecraftVersion() >= 21) {
            ReflectionObject connection = ReflectionObject.of(NMSUtil.getValueByFieldName(handle.getObject(), "PlayerConnection"));
            ReflectionObject networkManager = NMSUtil.getValueByFieldNameRObject(Class.forName("net.minecraft.server.network.ServerCommonPacketListenerImpl"), connection.getObject(), "NetworkManager");
            return new PlayerNMS(player, handle, connection, networkManager);
        }

        Object playerConnection = NMSUtil.getValueByFieldName(handle.getObject(), "PlayerConnection");
        Object networkManager =  NMSUtil.getValueByFieldName(playerConnection, "NetworkManager");

        return new PlayerNMS(player, handle, ReflectionObject.of(playerConnection), ReflectionObject.of(networkManager));

    }

    public void sendPacket(Object packet) {
        try {
            if (XG7Plugins.getMinecraftVersion() >= 19 && XG7Plugins.getMinecraftVersion() < 21) {
                playerConnection.getMethod("a", ReflectionClass.of("net.minecraft.network.protocol.Packet").getAClass()).invoke(packet);
                return;
            }
            playerConnection.getMethod("sendPacket", NMSUtil.getNMSClassViaVersion(17, "Packet", "network.protocol.Packet").getAClass()).invoke(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
