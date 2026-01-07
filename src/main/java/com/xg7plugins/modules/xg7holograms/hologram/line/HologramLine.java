package com.xg7plugins.modules.xg7holograms.hologram.line;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;

import java.util.Collections;
import java.util.HashMap;

public interface HologramLine {

    boolean levitate();
    float getSpacing();
    HashMap<EquipmentSlot, Item> getEquipment();

    int spawn(LivingHologram livingHologram, Location location);
    void update(LivingHologram livingHologram, int entityID);

    default void equip(LivingHologram livingHologram, int entityID, EquipmentSlot slot, Item item) {
        WrapperPlayServerEntityEquipment packet =
                new WrapperPlayServerEntityEquipment(
                        entityID,
                        Collections.singletonList(new Equipment(slot, item.toProtocolItemStack(livingHologram.getPlayer(), livingHologram.getHologram().getPlugin())))
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), packet);
    }

}
