package com.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.text.Text;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityTrackerEntry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class Hologram1_17_1_XX extends Hologram {

    public Hologram1_17_1_XX(Plugin plugin, List<String> lines, Location location) {
        super(plugin, lines, location);
    }

    @Override
    public void create(Player player) {

        for (int i = 0; i < lines.size(); i++) {
            Location spawnLocation = location.add(0, i * 0.3, 0);
            int entityID = ((AtomicInteger) ReflectionClass.of(Entity.class).getStaticField(getEntityCountField())).incrementAndGet();

            EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();

            dataWatcher.watch(0, (byte) 0x20);
            dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));
            dataWatcher.watch(3, true);
            dataWatcher.watch(5, true);

            PacketPlayOutSpawnEntity living = XG7Plugins.getMinecraftVersion() < 19 ?
                    new PacketPlayOutSpawnEntity(
                            entityID,
                            UUID.randomUUID(),
                            spawnLocation.getX(),
                            spawnLocation.getY(),
                            spawnLocation.getZ(),
                            0, 0,
                            EntityTypes.c,
                            0,
                            new Vec3D(0, 0, 0)
                    ) : (PacketPlayOutSpawnEntity) ReflectionClass.of(PacketPlayOutSpawnEntity.class)
                    .getConstructor(
                            int.class,
                            UUID.class,
                            double.class,
                            double.class,
                            double.class,
                            float.class,
                            float.class,
                            EntityTypes.class,
                            int.class,
                            Vec3D.class,
                            double.class
                    ).newInstance(
                            entityID,
                            UUID.randomUUID(),
                            spawnLocation.getX(),
                            spawnLocation.getY(),
                            spawnLocation.getZ(),
                            0, 0,
                            EntityTypes.d,
                            0,
                            new Vec3D(0, 0, 0),
                            0
                    ).getObject();


            PacketPlayOutEntityMetadata metadata = XG7Plugins.getMinecraftVersion() < 19 ?
                    new PacketPlayOutEntityMetadata(entityID, dataWatcher.getWatcher(), true)
                    :
                    (PacketPlayOutEntityMetadata) ReflectionClass.of(PacketPlayOutEntityMetadata.class)
                            .getConstructor(int.class, List.class).newInstance(entityID, dataWatcher.getWatcherRObject().getMethod("b").invoke()).getObject();


            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(living);
            playerNMS.sendPacket(metadata);

            ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

            ids.get(player.getUniqueId()).add(entityID);


        }


    }

    @Override
    public void update(Player player) {
        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();

            dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));

            PacketPlayOutEntityMetadata metadata = XG7Plugins.getMinecraftVersion() < 19 ?
                    new PacketPlayOutEntityMetadata(ids.get(player.getUniqueId()).get(i), dataWatcher.getWatcher(), true)
                    :
                    (PacketPlayOutEntityMetadata) ReflectionClass.of(PacketPlayOutEntityMetadata.class)
                            .getConstructor(int.class, List.class).newInstance(ids.get(player.getUniqueId()).get(i), dataWatcher.getWatcherRObject().getMethod("b").invoke()).getObject();

            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(metadata);

        }
    }

    @Override
    public void destroy(Player player) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(destroy);
        ids.remove(player.getUniqueId());
    }

    private String getEntityCountField() {
        switch (XG7Plugins.getMinecraftVersion()) {
            case 17:
                return "b";
            case 20:
                return "d";
            default:
                return "c";
        }
    }
}
