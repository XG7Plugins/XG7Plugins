package com.xg7plugins.utils.reflection.nms;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class PacketEvent {

    private final Player player;
    private final Packet packet;

}
