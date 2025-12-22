package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@ToString
public class ItemLine extends InvisibleArmorStandLine {

    private final Item item;

    public ItemLine(Item item, float spacing, boolean levitate) {
        super(spacing, levitate);

        this.item = item;
    }

    @Override
    public int spawn(LivingHologram livingHologram, Location location) {

        int superID = super.spawn(livingHologram, location);

        int entityID = SpigotReflectionUtil.generateEntityId();


        PacketWrapper<?> packet =  new WrapperPlayServerSpawnEntity(
                entityID,
                UUID.randomUUID(),
                EntityTypes.ITEM,
                location.getProtocolLocation(),
                0,0,
                Vector3d.zero()
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), packet);



        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
                entityID,
                Collections.singletonList(
                        new EntityData<>(
                                choseItemMetadataByVersion(),
                                EntityDataTypes.ITEMSTACK,
                                item.toProtocolItemStack(livingHologram.getPlayer(), livingHologram.getHologram().getPlugin())
                        )
                )
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadataPacket);

        PacketWrapper<?> mount = PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8) ?
                new WrapperPlayServerAttachEntity(
                        entityID,
                        superID,
                        false
                )
                :
                new WrapperPlayServerSetPassengers(
                    superID,
                    new int[]{ entityID }
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), mount);


        return entityID;
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {
        // No metadata to update for item entity
    }

    private int choseItemMetadataByVersion() {
        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        if (version.isOlderThanOrEquals(ServerVersion.V_1_7_10)) {
            return 10;
        } else if (version.isOlderThanOrEquals(ServerVersion.V_1_15_2)) {
            return 6;
        } else if (version.isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
            return 7;
        }

        return 8;
    }

}
