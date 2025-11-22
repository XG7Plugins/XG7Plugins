package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.location.Location;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;

import java.util.UUID;
import java.util.function.Consumer;

@Data
public class EntityLine implements HologramLine {

    private final EntityType entityType;
    private final float spacing;
    private final boolean levitate;

    @Override
    public boolean levitate() {
        return levitate;
    }

    @Override
    public int spawn(LivingHologram livingHologram, Location location) {

        int entityID = SpigotReflectionUtil.generateEntityId();

        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                entityID,
                UUID.randomUUID(),
                entityType,
                location.getProtocolLocation(),
                location.getYaw(),
                0,
                null
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), spawnEntityPacket);

        return entityID;
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {
        // No metadata to update for generic entity line
    }

}
