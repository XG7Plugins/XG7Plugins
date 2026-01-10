package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class EntityLine implements HologramLine {

    private final EntityType entityType;
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
    public int[] spawn(LivingHologram livingHologram, Location location) {

        int entityID = SpigotReflectionUtil.generateEntityId();

        PacketWrapper<?> packet = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17) ? new WrapperPlayServerSpawnLivingEntity(
                entityID,
                UUID.randomUUID(),
                entityType,
                location.getProtocolLocation(),
                location.getPitch(),
                Vector3d.zero(),
                new ArrayList<>()
        ) :
                new WrapperPlayServerSpawnEntity(
                        entityID,
                        UUID.randomUUID(),
                        entityType,
                        location.getProtocolLocation(),
                        0,0,
                        Vector3d.zero()
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), packet);

        return new int[]{entityID};
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {
        // No metadata to update for generic entity line
    }

}
