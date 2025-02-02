package com.xg7plugins.libs.xg7scores.scores.bossbar;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.*;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LegacyBossBar extends Score {

    private final float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();

    private static PacketClass packetPlayOutEntityTeleportClass;
    private static PacketClass packetPlayOutEntityMetadataClass;
    private static PacketClass packetPlayOutSpawnEntityLivingClass;
    private static PacketClass packetPlayOutEntityDestroyClass;

    private static ReflectionClass entityWitherClass;
    private static ReflectionClass worldClass;
    private static ReflectionClass entityLivingClass;

    static {
        try {
            packetPlayOutEntityTeleportClass = new PacketClass("PacketPlayOutEntityTeleport");
            packetPlayOutEntityMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");
            packetPlayOutSpawnEntityLivingClass = new PacketClass("PacketPlayOutSpawnEntityLiving");
            packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");

            entityWitherClass = NMSUtil.getNMSClass("EntityWither");
            worldClass = NMSUtil.getNMSClass("World");
            entityLivingClass = NMSUtil.getNMSClass("EntityLiving");
        } catch (Exception ignored) {

        }
    }


    @SneakyThrows
    public LegacyBossBar(long delay, List<String> text, String id, ScoreCondition condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        this.healthPercent = healthPercent;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            super.addPlayer(player);

            ReflectionObject wither = entityWitherClass.getConstructor(worldClass.getAClass())
                    .newInstance(worldClass.cast(ReflectionMethod.of(player.getWorld(), "getHandle").invoke()));

            Packet spawnPacket = new Packet(packetPlayOutSpawnEntityLivingClass, new Class<?>[]{entityLivingClass.getAClass()}, wither.getObject());

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(6, (healthPercent / 100) * 300);

            dataWatcher.watch(2, updateText.get(0));

            dataWatcher.watch(11, (byte) 1);
            dataWatcher.watch(3, (byte) 1);

            dataWatcher.watch(17, 0);
            dataWatcher.watch(18, 0);
            dataWatcher.watch(19, 0);

            dataWatcher.watch(20, 1000);
            dataWatcher.watch(0, (byte) (1 << 5));

            Packet packetPlayOutEntityMetadata = new Packet(packetPlayOutEntityMetadataClass);

            packetPlayOutEntityMetadata.setField("a", wither.getMethod("getId").invoke());
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcher().getMethod("c").invoke());

            entities.put(player.getUniqueId(), wither.getMethod("getId").invoke());

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(spawnPacket);

            playerNMS.sendPacket(packetPlayOutEntityMetadata);

        }, 2L);
    }

    @SneakyThrows
    @Override
    public synchronized void removePlayer(Player player) {

        if (!super.getPlayers().contains(player.getUniqueId())) return;
        if (!entities.containsKey(player.getUniqueId())) return;

        Packet packet = new Packet(packetPlayOutEntityDestroyClass, new Class<?>[]{int[].class}, new int[] {entities.get(player.getUniqueId())});

        PlayerNMS.cast(player).sendPacket(packet);

        super.removePlayer(player);
    }


    @SneakyThrows
    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (player.isDead()) continue;

            Location playerLocation = player.getLocation();
            Vector direction = playerLocation.getDirection();

            Location targetLocation = playerLocation.add(direction.multiply(40));

            Packet packet = new Packet(packetPlayOutEntityTeleportClass);

            packet.setField("a", entities.get(player.getUniqueId()));
            packet.setField("b", (int) (targetLocation.getX() * 32D));
            packet.setField("c", (int) (targetLocation.getY() * 32D));
            packet.setField("d", (int) (targetLocation.getZ() * 32D));
            packet.setField("e", (byte) (int) (targetLocation.getYaw() * 256F / 360F));
            packet.setField("f", (byte) (int) (targetLocation.getPitch() * 256F / 360F));

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packet);

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            String name = Text.detectLangOrText(plugin,player,updateText.get(indexUpdating)).join().getText();

            dataWatcher.watch(2, name);

            Packet packetPlayOutEntityMetadata = new Packet(packetPlayOutEntityMetadataClass);

            packetPlayOutEntityMetadata.setField("a", entities.get(player.getUniqueId()));
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcher().getMethod("c").invoke());
            playerNMS.sendPacket(packetPlayOutEntityMetadata);


        }

    }

}
