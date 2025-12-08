package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public class ArmorStandLine implements HologramLine {

    private final String line;
    private final float spacing;
    private final boolean levitate;

    @Override
    public boolean levitate() {
        return levitate;
    }

    @Override
    public int spawn(LivingHologram livingHologram, Location location) {

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        int entityID = SpigotReflectionUtil.generateEntityId();

        PacketWrapper<?> packet = version.isOlderThan(ServerVersion.V_1_17) ? new WrapperPlayServerSpawnLivingEntity(
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
                        0, 0, null
                );

        System.out.println("Spawning " + packet);

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), packet);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(entityID, HologramMetadataProvider.armorStandData());
        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadata);

        return entityID;
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                entityID,
                HologramMetadataProvider.updateArmorStandData(Text.detectLangs(livingHologram.getPlayer(), livingHologram.getHologram().getPlugin(), line, true).join())
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadata);
    }

}
