package com.xg7plugins.modules.xg7npcs.living;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public interface LivingNPC {

    Player getPlayer();
    NPC getNPC();

    LivingHologram getSpawnedHologram();
    void setSpawnedHologram(LivingHologram hologram);

    Location getCurrentLocation();
    void setCurrentLocation(Location location);

    int getSpawnedEntityID();
    void setSpawnedEntityID(int id);

    boolean isMoving();
    void setMoving(boolean moving);

    default void spawn() {

        if (getSpawnedEntityID() >= 0) return;

        setCurrentLocation(getNPC().getSpawnLocation());
        defaultSpawn(UUID.randomUUID(), new ArrayList<>());

        if (getNPC().getEquipments() != null) {
            getNPC().getEquipments().forEach(this::equip);
        }
    }

    default void defaultSpawn(UUID uuid, List<EntityData<?>> dataList) {

        int entityID = SpigotReflectionUtil.generateEntityId();
        System.out.println("✓ Entity ID gerado: " + entityID);

        PacketWrapper<?> spawnEntity = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17) ?
                getNPC().getEntityType().equals(EntityTypes.PLAYER) ?
                new WrapperPlayServerSpawnPlayer(
                        entityID,
                        uuid,
                        getCurrentLocation().toVector3d(),
                        getCurrentLocation().getYaw(),
                        getCurrentLocation().getPitch(),
                        Collections.emptyList()
                ) :
                new WrapperPlayServerSpawnLivingEntity(
                        entityID,
                        uuid,
                        getNPC().getEntityType(),
                        getCurrentLocation().getProtocolLocation(),
                        getCurrentLocation().getPitch(),
                        Vector3d.zero(),
                        new ArrayList<>()
                ) :
                new WrapperPlayServerSpawnEntity(
                        entityID,
                        uuid,
                        getNPC().getEntityType(),
                        getCurrentLocation().getProtocolLocation(),
                        0,0,
                        Vector3d.zero()
                );

        System.out.println("PACOTE DE SPAWN: " + spawnEntity);

        System.out.println("→ Enviando SpawnEntity packet");
        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), spawnEntity);

        setSpawnedEntityID(entityID);

        System.out.println("✓ spawnedEntityID setado: " + this.getSpawnedEntityID());

        WrapperPlayServerEntityHeadLook headLook = new WrapperPlayServerEntityHeadLook(
                entityID,
                getCurrentLocation().getYaw()
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), headLook);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                entityID,
                dataList
        );
        System.out.println("→ Enviando Metadata packet");
        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), metadata);

        if (getNPC().getEquipments() != null || !getNPC().getEquipments().isEmpty()) {
            getNPC().getEquipments().forEach(this::equip);
        }

        System.out.println("→ Spawnando hologram");
        getNPC().getHologram().setLocation(getCurrentLocation().clone());
        setSpawnedHologram(getNPC().getHologram().spawn(getPlayer()));
    }

    default void kill() {
        if (getSpawnedEntityID() < 0) return;

        WrapperPlayServerDestroyEntities entities = new WrapperPlayServerDestroyEntities(getSpawnedEntityID());

        getNPC().getHologram().kill(getPlayer());

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), entities);

        setSpawnedEntityID(-1);
        setCurrentLocation(null);
    }
    default void equip(EquipmentSlot slot, Item item) {
        if (getSpawnedEntityID() < 0) return;

        WrapperPlayServerEntityEquipment packet =
                new WrapperPlayServerEntityEquipment(
                        getSpawnedEntityID(),
                        Collections.singletonList(new Equipment(slot, item.toProtocolItemStack(getPlayer(), getNPC().getPlugin())))
                );


        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), packet);
    }
    default void lookAt(Location location) {
        if (getSpawnedEntityID() < 0 || getCurrentLocation() == null) return;
        if (isMoving()) return;

        Location currentLocation = getCurrentLocation();
        Player player = getPlayer();
        int spawnedEntityID = getSpawnedEntityID();


        double deltaX = location.getX() - currentLocation.getX();
        double deltaY = location.getY() - currentLocation.getY();
        double deltaZ = location.getZ() - currentLocation.getZ();

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan(deltaY / distanceXZ));

        // --- CORPO ---
        WrapperPlayServerEntityRotation body =
                new WrapperPlayServerEntityRotation(
                        spawnedEntityID,
                        yaw,
                        pitch,
                        false
                );

        WrapperPlayServerEntityHeadLook head =
                new WrapperPlayServerEntityHeadLook(
                        spawnedEntityID,
                        yaw
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, body);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, head);
    }
    default void move(Location location, double seconds) {
        setMoving(true);
        Location currentLocation = getCurrentLocation();
        Player player = getPlayer();
        int spawnedEntityID = getSpawnedEntityID();
        LivingHologram spawnedHologram = getSpawnedHologram();
        NPC npc = getNPC();

        if (!location.getWorld().getUID().equals(currentLocation.getWorld().getUID())) {
            return;
        }
        if (npc.getHologram() == null || spawnedHologram == null) {
            return;
        }
        if (seconds <= 0) {
            teleport(location);
            return;
        }

        Vector direction = location.toVector().subtract(currentLocation.toVector());
        int totalTicks = (int) Math.max(1, Math.round(20 * seconds));

        Vector step = direction.clone().multiply(1.0 / totalTicks);

        List<Integer> entitiesToMove = new ArrayList<>();
        entitiesToMove.add(spawnedEntityID);
        entitiesToMove.addAll(spawnedHologram.getSpawnedEntitiesID());

        AtomicInteger tick = new AtomicInteger();

        WrapperPlayServerEntityRotation body =
                new WrapperPlayServerEntityRotation(
                        spawnedEntityID,
                        location.getYaw(),
                        location.getPitch(),
                        false
                );

        WrapperPlayServerEntityHeadLook head =
                new WrapperPlayServerEntityHeadLook(
                        spawnedEntityID,
                        location.getYaw()
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, body);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, head);

        XG7Plugins.getAPI().taskManager().scheduleSyncRepeating(
                npc.getPlugin(),
                new BukkitTask() {
                    @Override
                    public void run() {
                        int current = tick.getAndIncrement();
                        if (current >= totalTicks) {
                            setMoving(false);
                            cancel();
                            return;
                        }
                        entitiesToMove.forEach(id -> {
                            WrapperPlayServerEntityRelativeMove packet =
                                    new WrapperPlayServerEntityRelativeMove(
                                            id,
                                            step.getX(), step.getY(), step.getZ(),
                                            false
                                    );
                            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                        });

                        currentLocation.add(step);
                    }
                },
                0L,
                1L
        );
    }

    default void teleport(Location location) {
        Location currentLocation = getCurrentLocation();
        Player player = getPlayer();
        int spawnedEntityID = getSpawnedEntityID();

        NPC npc = getNPC();

        if (!location.getWorld().getUID().equals(currentLocation.getWorld().getUID())) {
            return;
        }

        if (!location.getWorld().getUID().equals(currentLocation.getWorld().getUID())) {
            return;
        }

        WrapperPlayServerEntityTeleport packet =
                new WrapperPlayServerEntityTeleport(
                        spawnedEntityID,
                        location.getProtocolLocation(),
                        false
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);


        npc.getHologram().setLocation(location);
        npc.getHologram().kill(player);
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(() -> {
            WrapperPlayServerEntityRotation body =
                    new WrapperPlayServerEntityRotation(
                            spawnedEntityID,
                            location.getYaw(),
                            location.getPitch(),
                            false
                    );

            WrapperPlayServerEntityHeadLook head =
                    new WrapperPlayServerEntityHeadLook(
                            spawnedEntityID,
                            location.getYaw()
                    );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, body);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, head);

            npc.getHologram().spawn(player);

        }), 300L);

        setCurrentLocation(location.clone());
    }

}
