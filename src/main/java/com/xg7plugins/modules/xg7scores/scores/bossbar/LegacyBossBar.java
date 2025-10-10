package com.xg7plugins.modules.xg7scores.scores.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Getter
public class LegacyBossBar extends Score {

    private final float healthPercent;
    private final HashMap<UUID, Integer> entities = new HashMap<>();

    private static final AtomicInteger entityIds = new AtomicInteger(100000);

    @SneakyThrows
    public LegacyBossBar(long delay, List<String> text, String id, Function<Player, Boolean> condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        this.healthPercent = healthPercent;
    }

    @SneakyThrows
    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;

        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(() -> {

            super.addPlayer(player);

            int entityId = entityIds.getAndIncrement();
            entities.put(player.getUniqueId(), entityId);

            PacketContainer spawnPacket = null;
            try {
                spawnPacket = createWitherSpawnPacket(entityId);
                XG7PluginsAPI.protocolManager().sendServerPacket(player, spawnPacket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }), 2L);
    }

    @SneakyThrows
    @Override
    public synchronized void removePlayer(Player player) {
        if (!super.getPlayers().contains(player.getUniqueId())) return;
        if (!entities.containsKey(player.getUniqueId())) return;

        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{entities.get(player.getUniqueId())});
        XG7PluginsAPI.protocolManager().sendServerPacket(player, destroyPacket);

        super.removePlayer(player);
    }

    @SneakyThrows
    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null || player.isDead() || entities.get(player.getUniqueId()) == null) continue;

            Location playerLocation = Location.fromBukkit(player.getLocation());
            Vector direction = playerLocation.getDirection();
            Location targetLocation = playerLocation.add(direction.multiply(40));

            PacketContainer teleportPacket = createEntityTeleportPacket(entities.get(player.getUniqueId()), targetLocation);
            XG7PluginsAPI.protocolManager().sendServerPacket(player, teleportPacket);

            String name = Text.detectLangs(player, plugin, updateText.get(indexUpdating)).join().getText();
            PacketContainer metaPacket = createEntityMetadataPacket(entities.get(player.getUniqueId()), name);
            XG7PluginsAPI.protocolManager().sendServerPacket(player, metaPacket);
        }
    }

    private PacketContainer createWitherSpawnPacket(int entityId) throws Exception {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet.getIntegers().write(0, entityId);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.WITHER);
        packet.getDoubles().write(0, 0.0); 
        packet.getDoubles().write(1, 1.0); 
        packet.getDoubles().write(2, 0.0); 
        packet.getFloat().write(0, 0f); 
        packet.getFloat().write(1, 0f); 
        return packet;
    }

    private PacketContainer createEntityTeleportPacket(int entityId, Location location) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        packet.getBytes().write(0, (byte) (location.getYaw() * 256 / 360));
        packet.getBytes().write(1, (byte) (location.getPitch() * 256 / 360));
        packet.getBooleans().write(0, true);
        return packet;
    }

    private PacketContainer createEntityMetadataPacket(int entityId, String name) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entityId);

        WrappedDataWatcher watcher = new WrappedDataWatcher();

        watcher.setObject(0, (byte) (1 << 5), false);

        watcher.setObject(2, updateText.get(0), false);

        watcher.setObject(3, (byte) 1, false);

        watcher.setObject(11, (byte) 1, false);

        watcher.setObject(6, (float) ((healthPercent / 100) * 300), false);

        watcher.setObject(17, 0, false);
        watcher.setObject(18, 0, false);
        watcher.setObject(19, 0, false);
        watcher.setObject(20, 1000, false);

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return packet;
    }
}
