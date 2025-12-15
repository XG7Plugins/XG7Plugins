package com.xg7plugins.modules.xg7scores.scores.bossbar.legacy;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;

@Getter
public class LegacyBossBar extends Score {

    private final float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();


    @SneakyThrows
    public LegacyBossBar(long delay, List<String> text, String id, Function<Player, Boolean> condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        this.healthPercent = healthPercent;
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;

        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> {

            super.addPlayer(player);

            WrapperPlayServerSpawnLivingEntity spawnEntity = new WrapperPlayServerSpawnLivingEntity(
                    SpigotReflectionUtil.generateEntityId(),
                    UUID.randomUUID(),
                    EntityTypes.WITHER,
                    Location.of("world",0,1,0).getProtocolLocation(),
                    (float) 0,
                    Vector3d.zero(),
                    new BossBarMetadataProvider(healthPercent, updateText)
            );

            entities.put(player.getUniqueId(), spawnEntity.getEntityId());

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnEntity);

        }), 2L);
    }

    @SneakyThrows
    @Override
    public synchronized void removePlayer(Player player) {

        if (!super.getPlayers().contains(player.getUniqueId())) return;
        if (!entities.containsKey(player.getUniqueId())) return;

        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entities.get(player.getUniqueId()));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyEntities);

        super.removePlayer(player);
    }


    @SneakyThrows
    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (player.isDead()) continue;
            if (entities.get(player.getUniqueId()) == null) continue;

            Location playerLocation = Location.fromBukkit(player.getLocation());
            Vector direction = playerLocation.getDirection();

            Location targetLocation = playerLocation.add(direction.multiply(40));

            WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(
                    entities.get(player.getUniqueId()),
                    targetLocation.getProtocolLocation(),
                    true
            );


            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);


            String name = Text.detectLangs(player, plugin,updateText.get(indexUpdating)).getText();

            WrapperPlayServerEntityMetadata packetPlayOutEntityMetadata = new WrapperPlayServerEntityMetadata(
                    entities.get(player.getUniqueId()),
                    Collections.singletonList(new EntityData(2, EntityDataTypes.STRING, name))
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutEntityMetadata);

        }

    }

}