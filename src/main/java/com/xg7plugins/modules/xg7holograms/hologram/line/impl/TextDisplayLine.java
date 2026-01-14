package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.modules.xg7holograms.event.ClickAction;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.EntityDisplayOptions;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class TextDisplayLine implements HologramLine {

    private final String line;
    private final float spacing;
    private final boolean levitate;
    private final HashMap<EquipmentSlot, Item> equipment;

    private final EntityDisplayOptions displayOptions;

    @Override
    public boolean levitate() {
        return levitate;
    }

    @Override
    public int[] spawn(LivingHologram livingHologram, Location location) {

        Player player = livingHologram.getPlayer();

        int entityID = SpigotReflectionUtil.generateEntityId();
        int interactionID = SpigotReflectionUtil.generateEntityId();

        Location spawnLocation = new Location(location.getWorldName(), location.getX(), location.getY(), location.getZ(), displayOptions.getRotationX(), displayOptions.getRotationY());

        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                entityID,
                UUID.randomUUID(),
                EntityTypes.TEXT_DISPLAY,
                spawnLocation.getProtocolLocation(),
                0, 0, null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnEntityPacket);

        WrapperPlayServerSpawnEntity spawnInteractionPacket = new WrapperPlayServerSpawnEntity(
                interactionID,
                UUID.randomUUID(),
                EntityTypes.INTERACTION,
                spawnLocation.getProtocolLocation(),
                0, 0, null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnInteractionPacket);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(entityID, HologramMetadataProvider.textDisplayData(livingHologram, this));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);

        WrapperPlayServerEntityMetadata interactionMetadata = new WrapperPlayServerEntityMetadata(
                interactionID,
                HologramMetadataProvider.interactionData()
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, interactionMetadata);

        return new int[]{entityID, interactionID};
    }

    @Override
    public void update(LivingHologram livingHologram, LivingHologram.LivingLine livingLine) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                livingLine.getSpawnedEntities()[0],
                HologramMetadataProvider.textDisplayUpdateData(Text.detectLangs(livingHologram.getPlayer(), livingHologram.getHologram().getPlugin(), this.line, true)));

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadata);
    }
}
