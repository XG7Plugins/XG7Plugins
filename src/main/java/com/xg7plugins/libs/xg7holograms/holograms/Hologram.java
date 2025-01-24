package com.xg7plugins.libs.xg7holograms.holograms;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@AllArgsConstructor
public class Hologram {

    protected List<String> lines;
    @Setter
    protected Location location;
    protected String id;
    private Plugin plugin;

    public HologramState create(Player player) {
        Location initialLocation = location.clone();

        List<WrapperPlayServerSpawnEntity> armorStands = new ArrayList<>();

        for (String line : lines) {
            initialLocation = initialLocation.add(0, 0.3, 0).clone();
            WrapperPlayServerSpawnEntity armorStand = new WrapperPlayServerSpawnEntity(
                    SpigotReflectionUtil.generateEntityId(),
                    UUID.randomUUID(),
                    EntityTypes.ARMOR_STAND,
                    initialLocation.getProtocolLocation(),
                    0,
                    0,
                    null
            );
            List<EntityData> entityData = new ArrayList<>();

            entityData.add(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20));
            entityData.add(new EntityData(2, EntityDataTypes.STRING, line));
            entityData.add(new EntityData(10, EntityDataTypes.STRING, line));
            entityData.add(new EntityData(3, EntityDataTypes.BOOLEAN, true));
            entityData.add(new EntityData(5, EntityDataTypes.BOOLEAN, true));

            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                    armorStand.getEntityId(),
                    entityData
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, armorStand);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);

            armorStands.add(armorStand);
        }

        return new HologramState(this, armorStands, player);
    }

    public void update(HologramState state) {
        for (int i = 0; i < state.getHologramEntities().size(); i++) {

            List<EntityData> entityData = new ArrayList<>();

            String textFormatted = Text.detectLangOrText(plugin,state.getPlayer(),lines.get(i)).join().getText();

            entityData.add(new EntityData(2, EntityDataTypes.STRING, textFormatted));
            entityData.add(new EntityData(10, EntityDataTypes.STRING, textFormatted));

            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(state.getHologramEntities().get(i).getEntityId(), entityData);

            PacketEvents.getAPI().getPlayerManager().sendPacket(state.getPlayer(), metadata);
        }
    }

    public void destroy(HologramState state) {

        PacketEvents.getAPI().getPlayerManager().sendPacket(state.getPlayer(), new WrapperPlayServerDestroyEntities(state.getHologramEntities().stream().mapToInt(WrapperPlayServerSpawnEntity::getEntityId).toArray()));

    }
}
