package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;

import java.util.*;

@Data
public class InvisibleArmorStandLine implements HologramLine {

    private final float spacing;
    private final boolean levitate;
    private final HashMap<EquipmentSlot, Item> equipment;

    @Override
    public boolean levitate() {
        return levitate;
    }

    @Override
    public HashMap<EquipmentSlot, Item> getEquipment() {
        return equipment;
    }

    @Override
    public int spawn(LivingHologram livingHologram, Location location) {

        int entityID = SpigotReflectionUtil.generateEntityId();

        PacketWrapper<?> packet = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17) ? new WrapperPlayServerSpawnLivingEntity(
                entityID,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                location.getProtocolLocation(),
                location.getPitch(),
                Vector3d.zero(),
                new ArrayList<>()
        ) :
                new WrapperPlayServerSpawnEntity(
                        entityID,
                        UUID.randomUUID(),
                        EntityTypes.ARMOR_STAND,
                        location.getProtocolLocation(),
                        0,0,
                        Vector3d.zero()
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), packet);

        List<EntityData<?>> armorStandData = new ArrayList<>();

        armorStandData.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20)); // Invisible

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(entityID, Collections.singletonList(new EntityData<>(0, EntityDataTypes.BYTE,  (byte) 0x20)));
        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadata);

        return entityID;
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {
        // No update needed for invisible armor stand
    }

}
