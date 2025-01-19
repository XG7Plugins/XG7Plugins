package com.xg7plugins.libs.xg7scores.scores.bossbar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LegacyBossBar extends Score {

    private final float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();

    @SneakyThrows
    public LegacyBossBar(long delay, List<String> text, String id, ScoreCondition condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        this.healthPercent = healthPercent;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;


        super.addPlayer(player);

        WrapperPlayServerSpawnEntity wither = new WrapperPlayServerSpawnEntity(
                SpigotReflectionUtil.generateEntityId(),
                UUID.randomUUID(),
                EntityTypes.WITHER,
                Location.fromPlayer(player).add(0, 40, 0).getProtocolLocation(),
                0,
                0,
                null
        );
        List<EntityData> entityData = new ArrayList<>();

        entityData.add(new EntityData(6, EntityDataTypes.FLOAT, (healthPercent / 100) * 300));

        entityData.add(new EntityData(2, EntityDataTypes.STRING, updateText.get(0)));
        entityData.add(new EntityData(10, EntityDataTypes.STRING, updateText.get(0)));

        entityData.add(new EntityData(11, EntityDataTypes.BOOLEAN, true));
        entityData.add(new EntityData(3, EntityDataTypes.BOOLEAN, true));

        entityData.add(new EntityData(17, EntityDataTypes.BYTE, 0));
        entityData.add(new EntityData(18, EntityDataTypes.BYTE, 0));
        entityData.add(new EntityData(19, EntityDataTypes.BYTE, 0));

        entityData.add(new EntityData(20, EntityDataTypes.INT, 1000));
        entityData.add(new EntityData(0, EntityDataTypes.BYTE, (byte) (1 << 5)));

        WrapperPlayServerEntityMetadata packetPlayOutEntityMetadata = new WrapperPlayServerEntityMetadata(
                wither.getEntityId(),
                entityData
        );

        entities.put(player.getUniqueId(), wither.getEntityId());

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wither);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutEntityMetadata);

    }

    @SneakyThrows
    @Override
    public synchronized void removePlayer(Player player) {

        if (!super.getPlayers().contains(player.getUniqueId())) return;
        if (!entities.containsKey(player.getUniqueId())) return;

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entities.get(player.getUniqueId())));

        super.removePlayer(player);
    }


    @SneakyThrows
    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (player.isDead()) continue;

            Location playerLocation = Location.fromPlayer(player);
            Vector direction = playerLocation.getDirection();

            Location targetLocation = playerLocation.add(direction.multiply(40));


            WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(
                    entities.get(player.getUniqueId()),
                    targetLocation.getProtocolLocation(),
                    true
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

            String name = Text.detectLangOrText(plugin,player,updateText.get(indexUpdating)).join().getText();

            List<EntityData> entityData = new ArrayList<>();

            entityData.add(new EntityData(6, EntityDataTypes.FLOAT, (healthPercent / 100) * 300);

            entityData.add(new EntityData(2, EntityDataTypes.STRING, name));
            entityData.add(new EntityData(10, EntityDataTypes.STRING, name));

            WrapperPlayServerEntityMetadata packetPlayOutEntityMetadata = new WrapperPlayServerEntityMetadata(
                    entities.get(player.getUniqueId()),
                    entityData
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutEntityMetadata);


        }

    }
}
