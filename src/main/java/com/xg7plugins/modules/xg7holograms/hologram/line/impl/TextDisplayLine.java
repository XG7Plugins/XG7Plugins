package com.xg7plugins.modules.xg7holograms.hologram.line.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.modules.xg7holograms.event.ClickAction;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.HologramMetadataProvider;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class TextDisplayLine implements HologramLine {

    private final String line;
    private final float spacing;
    private final boolean levitate;

    private final Vector3f scale;
    private final float rotationX;
    private final float rotationY;
    private final boolean background;
    private final Color backgroundColor;
    private final boolean shadow;
    private final boolean seeThrough;
    private final Billboard billboard;
    private final Alignment alignment;

    @Override
    public boolean levitate() {
        return levitate;
    }

    public enum Billboard {
        FIXED,
        VERTICAL,
        HORIZONTAL,
        CENTER
    }
    public enum Alignment {
        LEFT,
        RIGHT,
        CENTER
    }

    @Override
    public int spawn(LivingHologram livingHologram, Location location) {

        Player player = livingHologram.getPlayer();

        int entityID = SpigotReflectionUtil.generateEntityId();
        int armorStandID = SpigotReflectionUtil.generateEntityId();

        Location spawnLocation = new Location(location.getWorldName(), location.getX(), location.getY(), location.getZ(), rotationX, rotationY);

        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                entityID,
                UUID.randomUUID(),
                EntityTypes.TEXT_DISPLAY,
                spawnLocation.getProtocolLocation(),
                0, 0, null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnEntityPacket);

        WrapperPlayServerSpawnEntity spawnArmorStandPacket = new WrapperPlayServerSpawnEntity(
                armorStandID,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                spawnLocation.getProtocolLocation(),
                0, 0, null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnArmorStandPacket);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(entityID, HologramMetadataProvider.textDisplayData(livingHologram, this));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);

        WrapperPlayServerEntityMetadata armorStandMetadata = new WrapperPlayServerEntityMetadata(armorStandID, HologramMetadataProvider.triggerArmorStandData());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, armorStandMetadata);

        return entityID;
    }

    @Override
    public void update(LivingHologram livingHologram, int entityID) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                entityID,
                HologramMetadataProvider.textDisplayUpdateData(Text.detectLangs(livingHologram.getPlayer(), livingHologram.getHologram().getPlugin(), this.line, true).join()));

        PacketEvents.getAPI().getPlayerManager().sendPacket(livingHologram.getPlayer(), metadata);
    }
}
