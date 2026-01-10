package com.xg7plugins.modules.xg7npcs.living.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.living.NPCMetaProvider;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.DisplayNPC;
import com.xg7plugins.utils.location.Location;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//TODO
@Data
public class LivingDisplayNPC implements LivingNPC {

    private final Player player;
    private final DisplayNPC npc;

    private LivingHologram spawnedHologram = null;

    private Location currentLocation = null;

    private boolean moving = false;

    private int[] spawnedEntitiesID = null;

    @Override
    public NPC getNPC() {
        return npc;
    }

    @Override
    public void spawn() {
        if (getSpawnedEntitiesID() != null) return;

        setCurrentLocation(getNPC().getSpawnLocation());

        defaultSpawn(UUID.randomUUID(), NPCMetaProvider.entityDisplayData(this));

        int armorStandID = SpigotReflectionUtil.generateEntityId();

        WrapperPlayServerSpawnEntity spawnInteractionPacket = new WrapperPlayServerSpawnEntity(
                armorStandID,
                UUID.randomUUID(),
                EntityTypes.INTERACTION,
                getNPC().getSpawnLocation().getProtocolLocation(),
                0, 0, null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnInteractionPacket);

        WrapperPlayServerEntityMetadata interactionMetadata = new WrapperPlayServerEntityMetadata(
                armorStandID,
                HologramMetadataProvider.interactionData()
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, interactionMetadata);

        setSpawnedEntitiesID(new int[]{getSpawnedEntitiesID()[0], armorStandID});

        System.out.println("IDS " + Arrays.toString(getSpawnedEntitiesID()));

    }
}
