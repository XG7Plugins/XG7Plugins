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

public class Hologram1_8_1_16 extends Hologram {

    private static ReflectionClass entityArmorStandClass;
    private static ReflectionClass worldClass;
    private static ReflectionClass entityLivingClass;

    private static PacketClass packetPlayOutSpawnEntityLivingClass;
    private static PacketClass packetMetadataClass;
    private static PacketClass packetDestroyClass;

    static {
        try {
            entityArmorStandClass = NMSUtil.getNMSClass("EntityArmorStand");
            worldClass = NMSUtil.getNMSClass("World");
            entityLivingClass = NMSUtil.getNMSClass("EntityLiving");

            packetPlayOutSpawnEntityLivingClass = new PacketClass("PacketPlayOutSpawnEntityLiving");
            packetMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");
            packetDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public Hologram1_8_1_16(Plugin plugin, String id, List<String> lines, Location location) {
        super(plugin, id, lines, location);
    }

    @Override
    public void create(Player player) {
        ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();
        for (int i = 0; i < lines.size(); i++) {

            Location spawnLocation = location.add(0, i * 0.3, 0);

            ReflectionObject armorStand = entityArmorStandClass
                    .getConstructor(worldClass.getAClass(), double.class, double.class, double.class)
                    .newInstance(nmsWorld.getObject(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(0, (byte) 0x20);
            dataWatcher.watch(2, Text.detectLangOrText(XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance()), player, lines.get(i)).join().getText());
            dataWatcher.watch(3, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);
            dataWatcher.watch(5, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);

            Packet spawnPacket = new Packet(packetPlayOutSpawnEntityLivingClass, new Class<?>[]{entityLivingClass.getAClass()}, armorStand.getObject());

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(spawnPacket);

            Packet packetPlayOutEntityMetadata = new Packet(packetMetadataClass);

            packetPlayOutEntityMetadata.setField("a", armorStand.getMethod("getId").invoke());
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcher().getMethod("c").invoke());

            playerNMS.sendPacket(packetPlayOutEntityMetadata);

            ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

            ids.get(player.getUniqueId()).add(armorStand.getMethod("getId").invoke());
        }

    }

    @Override
    public void destroy(Player player) {

        if (!ids.containsKey(player.getUniqueId())) return;

        Packet packet = new Packet(packetDestroyClass, new Class[]{int[].class}, (int[]) ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packet);
        ids.remove(player.getUniqueId());
    }

    @Override
    public void update(Player player) {

        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(2, Text.detectLangOrText(XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance()), player, lines.get(i)).join().getText());

            Packet packetPlayOutEntityMetadata = new Packet(packetMetadataClass);

            packetPlayOutEntityMetadata.setField("a", ids.get(player.getUniqueId()).get(i));
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcher().getMethod("c").invoke());
            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(packetPlayOutEntityMetadata);

        }

    }
}
