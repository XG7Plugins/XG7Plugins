package com.xg7plugins.libs.xg7scores.scores.bossbar;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.EntityDataWatcher;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
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

    private static final ReflectionObject packetPlayOutEntityTeleport = NMSUtil.getNMSClass("PacketPlayOutEntityTeleport").newInstance();
    private static final ReflectionObject packetPlayOutEntityMetadata = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata").newInstance();

    @SneakyThrows
    public LegacyBossBar(long delay, List<String> text, String id, ScoreCondition condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        this.healthPercent = healthPercent;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void addPlayer(Player player) {
        if (!super.getPlayers().contains(player)) {

            super.addPlayer(player);

            ReflectionObject wither = NMSUtil.getNMSClass("EntityWither")
                    .getConstructor(NMSUtil.getNMSClass("World").getAClass())
                    .newInstance(NMSUtil.getNMSClass("World").cast(ReflectionMethod.of(player.getWorld(), "getHandle").invoke()));

            ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving")
                    .getConstructor(NMSUtil.getNMSClass("EntityLiving").getAClass())
                    .newInstance(wither.getObject());

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(6, (healthPercent / 100) * 300);

            dataWatcher.watch( 10, updateText.get(0));
            dataWatcher.watch( 2, updateText.get(0));

            dataWatcher.watch(11, (byte) 1);
            dataWatcher.watch(3, (byte) 1);

            dataWatcher.watch(17, 0);
            dataWatcher.watch(18, 0);
            dataWatcher.watch(19, 0);

            dataWatcher.watch(20, 1000);
            dataWatcher.watch(0, (byte) (1 << 5));

            packetPlayOutEntityMetadata.setField("a", wither.getMethod("getId").invoke());
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcherAsARObject().getMethod("c").invoke());

            entities.put(player.getUniqueId(), wither.getMethod("getId").invoke());

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packet.getObject());

            playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());
        }

    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy").
                getConstructor(int[].class).newInstance(new int[]{entities.get(player.getUniqueId())});

        PlayerNMS.cast(player).sendPacket(packet.getObject());

        entities.remove(player.getUniqueId());
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

            packetPlayOutEntityTeleport.setField("a", entities.get(player.getUniqueId()));
            packetPlayOutEntityTeleport.setField("b", (int) (targetLocation.getX() * 32D));
            packetPlayOutEntityTeleport.setField("c", (int) (targetLocation.getY() * 32D));
            packetPlayOutEntityTeleport.setField("d", (int) (targetLocation.getZ() * 32D));
            packetPlayOutEntityTeleport.setField("e", (byte) (int) (targetLocation.getYaw() * 256F / 360F));
            packetPlayOutEntityTeleport.setField("f", (byte) (int) (targetLocation.getPitch() * 256F / 360F));

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packetPlayOutEntityTeleport.getObject());

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(10, Text.format(updateText.get(indexUpdating),plugin).getWithPlaceholders(player));
            dataWatcher.watch(2, Text.format(updateText.get(indexUpdating),plugin).getWithPlaceholders(player));

            packetPlayOutEntityMetadata.setField("a", entities.get(player.getUniqueId()));
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcherAsARObject().getMethod("c").invoke());

            playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());


        }

    }
}
