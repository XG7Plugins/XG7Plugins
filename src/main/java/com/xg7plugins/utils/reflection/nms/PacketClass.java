package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import lombok.Getter;

@Getter
public class PacketClass {

    private final ReflectionClass reflectionClass;
    private String packetName;

    public PacketClass(String name) {
        this.packetName = name;
        this.reflectionClass = NMSUtil.getNMSClassViaVersion(17, name, "network.protocol.game." + name);
    }
    public PacketClass(String name, String newName) {
        this.packetName = XG7Plugins.getMinecraftVersion() >= 17 ? newName : name;
        this.reflectionClass = NMSUtil.getNMSClassViaVersion(17, name, newName);
    }
}
