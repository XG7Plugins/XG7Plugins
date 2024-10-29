package com.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram1_8_1_16 extends Hologram {



    public Hologram1_8_1_16(Plugin plugin, String id, List<String> lines, Location location) {
        super(plugin, id, lines, location);
    }

    @Override
    public void create(Player player) {
        try {
            ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();
            for (int i = 0; i < lines.size(); i++) {

                Location spawnLocation = location.add(0,i * 0.3,0);

                ReflectionObject armorStand = NMSUtil.getNMSClass("EntityArmorStand")
                        .getConstructor(NMSUtil.getNMSClass("World").getAClass(), double.class, double.class, double.class)
                        .newInstance(nmsWorld.getObject(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

                EntityDataWatcher dataWatcher = new EntityDataWatcher();

                dataWatcher.watch(0 , (byte) 0x20);
                dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));
                dataWatcher.watch(3, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);
                dataWatcher.watch(5, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);


                ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving")
                        .getConstructor(NMSUtil.getNMSClass("EntityLiving").getAClass())
                        .newInstance(armorStand.getObject());

                PlayerNMS playerNMS = PlayerNMS.cast(player);
                playerNMS.sendPacket(packet.getObject());

                ReflectionObject packetPlayOutEntityMetadata = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata").newInstance();
                packetPlayOutEntityMetadata.setField("a", armorStand.getMethod("getId").invoke());
                packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcherAsARObject().getMethod("c").invoke());

                playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());

                ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

                ids.get(player.getUniqueId()).add(armorStand.getMethod("getId").invoke());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destroy(Player player) {

        if (!ids.containsKey(player.getUniqueId())) return;

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy")
                .getConstructor(int[].class)
                .newInstance(ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packet.getObject());
        ids.remove(player.getUniqueId());
    }

    @Override
    public void update(Player player) {

        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));

            ReflectionObject packetPlayOutEntityMetaData = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, dataWatcher.getWatcherAsARObject().getObjectClass(), boolean.class)
                    .newInstance(ids.get(player.getUniqueId()).get(i), dataWatcher.getWatcher(), true);

            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(packetPlayOutEntityMetaData.getObject());

        }

    }
}
