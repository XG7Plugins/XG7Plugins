package com.xg7plugins.temp.xg7holograms.holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.*;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class Hologram1_17_1_XX extends Hologram {

    private static final ReflectionClass vector3dClass = NMSUtil.getNewerNMSClass("world.phys.Vec3D");
    private static final ReflectionClass entityTypesClass = NMSUtil.getNewerNMSClass("world.entity.EntityTypes");
    private static final PacketClass packetPlayOutSpawnEntityClass = new PacketClass("PacketPlayOutSpawnEntity");
    private static final PacketClass packetPlayOutEntityMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");
    private static final PacketClass packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");

    public Hologram1_17_1_XX(Plugin plugin, String id, List<String> lines, Location location) {
        super(plugin, id, lines, location);
    }

    @Override
    public void create(Player player) {
         for (int i = 0; i < lines.size(); i++) {
             Location spawnLocation = location.add(0, i * 0.3, 0);
             int entityID = ((AtomicInteger) NMSUtil.getNewerNMSClass("world.entity.Entity").getStaticField(getEntityCountField())).incrementAndGet();

             EntityDataWatcher dataWatcher = new EntityDataWatcher();

             dataWatcher.watch(0, (byte) 0x20);
             dataWatcher.watch(2, Text.detectLangOrText(XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance()), player, lines.get(i)).join().getText());
             dataWatcher.watch(3, true);
             dataWatcher.watch(5, true);

             Packet spawnPacket = XG7Plugins.getMinecraftVersion() < 19 ? new Packet(packetPlayOutSpawnEntityClass,
                     entityID,
                     UUID.randomUUID(),
                     spawnLocation.getX(),
                     spawnLocation.getY(),
                     spawnLocation.getZ(),
                     0f,
                     0f,
                     entityTypesClass.getStaticField("c"),
                     0,
                     vector3dClass.getConstructor(double.class, double.class, double.class).newInstance(0, 0, 0).getObject()
             ) :
                     new Packet(packetPlayOutSpawnEntityClass,
                             entityID,
                             UUID.randomUUID(),
                             spawnLocation.getX(),
                             spawnLocation.getY(),
                             spawnLocation.getZ(),
                             0f,
                             0f,
                             entityTypesClass.getStaticField("d"),
                             0,
                             vector3dClass.getConstructor(double.class, double.class, double.class).newInstance(0, 0, 0).getObject(),
                             0.0
                     );

             Packet metadataPacket = XG7Plugins.getMinecraftVersion() < 19 ?
                     new Packet(packetPlayOutEntityMetadataClass,
                     entityID,
                     dataWatcher.getWatcher().getObject(),
                     true
             ) :
                     new Packet(packetPlayOutEntityMetadataClass,
                             new Class[]{int.class, List.class},
                             entityID,
                             dataWatcher.getWatcher().getMethod("b").invoke()
                     );

             PlayerNMS playerNMS = PlayerNMS.cast(player);
             playerNMS.sendPacket(spawnPacket);
             playerNMS.sendPacket(metadataPacket);

             ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

             ids.get(player.getUniqueId()).add(entityID);


         }

    }

    @Override
    public void update(Player player) {
        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(2, Text.detectLangOrText(XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance()), player, lines.get(i)).join().getText());

            Packet metadataPacket = XG7Plugins.getMinecraftVersion() < 19 ?
                    new Packet(packetPlayOutEntityMetadataClass,
                            ids.get(player.getUniqueId()).get(i),
                            dataWatcher.getWatcher().getObject(),
                            true
                    ) :
                    new Packet(packetPlayOutEntityMetadataClass,
                            new Class[]{int.class, List.class},
                            ids.get(player.getUniqueId()).get(i),
                            dataWatcher.getWatcher().getMethod("b").invoke()
                    );

            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(metadataPacket);

        }
    }

    @Override
    public void destroy(Player player) {
        if (!ids.containsKey(player.getUniqueId())) return;

        Packet destroy = new Packet(packetPlayOutEntityDestroyClass, new Class[]{int[].class}, (int[]) ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());

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
