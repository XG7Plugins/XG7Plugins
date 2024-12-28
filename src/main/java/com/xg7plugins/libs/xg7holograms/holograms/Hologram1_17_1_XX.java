package com.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class Hologram1_17_1_XX extends Hologram {

    private static final ReflectionClass vector3dClass = NMSUtil.getNewerNMSClass("world.phys.Vec3D");
    private static final ReflectionClass entityTypesClass = NMSUtil.getNewerNMSClass("world.entity.EntityTypes");
    private static final ReflectionClass packetPlayOutSpawnEntityClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutSpawnEntity");
    private static final ReflectionClass packetPlayOutEntityMetadataClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityMetadata");
    private static final ReflectionClass packetPlayOutEntityDestroyClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityDestroy");


    public Hologram1_17_1_XX(Plugin plugin, String id, List<String> lines, Location location) {
        super(plugin, id, lines, location);
    }

    @Override
    public void create(Player player) {
         for (int i = 0; i < lines.size(); i++) {
             Location spawnLocation = location.add(0, i * 0.3, 0);
             int entityID = ((AtomicInteger) NMSUtil.getNewerNMSClass("world.entity.Entity").getStaticField(getEntityCountField())).incrementAndGet();

             EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();

             dataWatcher.watch(0, (byte) 0x20);
             dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));
             dataWatcher.watch(3, true);
             dataWatcher.watch(5, true);

             ReflectionObject spawn = XG7Plugins.getMinecraftVersion() < 19 ?
                     packetPlayOutSpawnEntityClass
                             .getConstructor(
                                     int.class,
                                     UUID.class,
                                     double.class,
                                     double.class,
                                     double.class,
                                     float.class,
                                     float.class,
                                     entityTypesClass.getAClass(),
                                     int.class,
                                     vector3dClass.getAClass()
                             ).newInstance(
                                     entityID,
                                     UUID.randomUUID(),
                                     spawnLocation.getX(),
                                     spawnLocation.getY(),
                                     spawnLocation.getZ(),
                                     0,
                                     0,
                                     entityTypesClass.getStaticField("c"),
                                     0,
                                     vector3dClass.getConstructor(double.class, double.class, double.class).newInstance(0, 0, 0).getObject()
                             )
                     :
                     packetPlayOutSpawnEntityClass
                             .getConstructor(
                                     int.class,
                                     UUID.class,
                                     double.class,
                                     double.class,
                                     double.class,
                                     float.class,
                                     float.class,
                                     entityTypesClass.getAClass(),
                                     int.class,
                                     vector3dClass.getAClass(),
                                     double.class
                             ).newInstance(
                                     entityID,
                                     UUID.randomUUID(),
                                     spawnLocation.getX(),
                                     spawnLocation.getY(),
                                     spawnLocation.getZ(),
                                     0,
                                     0,
                                     entityTypesClass.getStaticField("d"),
                                     0,
                                     vector3dClass.getConstructor(double.class, double.class, double.class).newInstance(0, 0, 0).getObject(),
                                     0
                             );

             ReflectionObject metadata = XG7Plugins.getMinecraftVersion() < 19 ?
                     packetPlayOutEntityMetadataClass
                             .getConstructor(int.class, dataWatcher.getWatcher().getObjectClass(), boolean.class)
                             .newInstance(entityID, dataWatcher.getWatcher().getObject(), true)
                     :
                     packetPlayOutEntityMetadataClass
                             .getConstructor(int.class, List.class)
                             .newInstance(entityID, dataWatcher.getWatcher().getMethod("b").invoke());


             PlayerNMS playerNMS = PlayerNMS.cast(player);
             playerNMS.sendPacket(spawn.getObject());
             playerNMS.sendPacket(metadata.getObject());

             ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

             ids.get(player.getUniqueId()).add(entityID);


         }




    }

    @Override
    public void update(Player player) {
        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();

            dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));

            ReflectionObject metadata = XG7Plugins.getMinecraftVersion() < 19 ?
                    packetPlayOutEntityMetadataClass
                            .getConstructor(int.class, dataWatcher.getWatcher().getObjectClass(), boolean.class)
                            .newInstance(ids.get(player.getUniqueId()).get(i), dataWatcher.getWatcher().getObject(), true)
                    :
                    packetPlayOutEntityMetadataClass
                            .getConstructor(int.class, List.class)
                            .newInstance(ids.get(player.getUniqueId()).get(i), dataWatcher.getWatcher().getMethod("b").invoke());

            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(metadata.getObject());

        }
    }

    @Override
    public void destroy(Player player) {
        if (!ids.containsKey(player.getUniqueId())) return;

        ReflectionObject destroy = packetPlayOutEntityDestroyClass
                .getConstructor(int[].class)
                .newInstance(ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());

        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(destroy.getObject());
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
