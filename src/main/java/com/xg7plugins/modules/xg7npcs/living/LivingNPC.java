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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface LivingNPC {

    Player getPlayer();
    NPC getNPC();

    LivingHologram getSpawnedHologram();
    void setSpawnedHologram(LivingHologram hologram);

    Location getCurrentLocation();
    void setCurrentLocation(Location location);

    // It's an array for Interaction entities (like hitboxes)
    int[] getSpawnedEntitiesID();
    void setSpawnedEntitiesID(int[] ids);

    boolean isMoving();
    void setMoving(boolean moving);

    default void spawn() {
        if (getSpawnedEntitiesID() != null) return;

        setCurrentLocation(getNPC().getSpawnLocation());
        defaultSpawn(UUID.randomUUID(), new ArrayList<>());
    }

    default void defaultSpawn(UUID uuid, List<EntityData<?>> dataList) {

        int entityID = SpigotReflectionUtil.generateEntityId();

        PacketWrapper<?> spawnEntity = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17) ?
                getNPC().getEntityType().equals(EntityTypes.PLAYER) || getNPC().getEntityType().equals(EntityTypes.MANNEQUIN) ?
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

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), spawnEntity);

        setSpawnedEntitiesID(new int[]{entityID});

        WrapperPlayServerEntityHeadLook headLook = new WrapperPlayServerEntityHeadLook(
                entityID,
                getCurrentLocation().getYaw()
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), headLook);

        if (getNPC().isGlow()) {
            dataList.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x40));
        }

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(
                entityID,
                dataList
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), metadata);

        if (getNPC().getEquipments() != null || !getNPC().getEquipments().isEmpty()) {
            getNPC().getEquipments().forEach(this::equip);
        }

        getNPC().getHologram().setLocation(getCurrentLocation().clone());
        setSpawnedHologram(getNPC().getHologram().spawn(getPlayer()));
    }

    default void kill() {
        if (getSpawnedEntitiesID() == null) return;

        WrapperPlayServerDestroyEntities entities = new WrapperPlayServerDestroyEntities(getSpawnedEntitiesID());

        getNPC().getHologram().kill(getPlayer());

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), entities);

        setSpawnedEntitiesID(null);
        setCurrentLocation(null);
    }
    default void equip(EquipmentSlot slot, Item item) {
        if (getSpawnedEntitiesID() == null) return;

        int mainEntityId = getSpawnedEntitiesID()[0];

        WrapperPlayServerEntityEquipment packet =
                new WrapperPlayServerEntityEquipment(
                        mainEntityId,
                        Collections.singletonList(new Equipment(slot, item.toProtocolItemStack(getPlayer(), getNPC().getPlugin())))
                );


        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), packet);
    }
    default void lookAt(Location location) {
        if (getSpawnedEntitiesID() == null || getCurrentLocation() == null) return;
        if (isMoving()) return;

        Location currentLocation = getCurrentLocation();
        Player player = getPlayer();
        int mainEntityId = getSpawnedEntitiesID()[0];

        double deltaX = location.getX() - currentLocation.getX();
        double deltaY = location.getY() - currentLocation.getY();
        double deltaZ = location.getZ() - currentLocation.getZ();

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan(deltaY / distanceXZ));

        // --- CORPO ---
        WrapperPlayServerEntityRotation body =
                new WrapperPlayServerEntityRotation(
                        mainEntityId,
                        yaw,
                        pitch,
                        false
                );

        WrapperPlayServerEntityHeadLook head =
                new WrapperPlayServerEntityHeadLook(
                        mainEntityId,
                        yaw
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, body);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, head);
    }
    default void move(Location location, double seconds) {
        setMoving(true);
        Location currentLocation = getCurrentLocation();
        Player player = getPlayer();
        int mainEntityId = getSpawnedEntitiesID()[0];
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
        entitiesToMove.add(mainEntityId);
        entitiesToMove.addAll(spawnedHologram.getAllSpawnedEntitiesID());

        AtomicInteger tick = new AtomicInteger();

        WrapperPlayServerEntityRotation body =
                new WrapperPlayServerEntityRotation(
                        mainEntityId,
                        location.getYaw(),
                        location.getPitch(),
                        false
                );

        WrapperPlayServerEntityHeadLook head =
                new WrapperPlayServerEntityHeadLook(
                        mainEntityId,
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
        int mainEntityId = getSpawnedEntitiesID()[0];

        NPC npc = getNPC();

        if (!location.getWorld().getUID().equals(currentLocation.getWorld().getUID())) {
            return;
        }

        if (!location.getWorld().getUID().equals(currentLocation.getWorld().getUID())) {
            return;
        }

        WrapperPlayServerEntityTeleport packet =
                new WrapperPlayServerEntityTeleport(
                        mainEntityId,
                        location.getProtocolLocation(),
                        false
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);


        npc.getHologram().setLocation(location);
        npc.getHologram().kill(player);
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(() -> {
            WrapperPlayServerEntityRotation body =
                    new WrapperPlayServerEntityRotation(
                            mainEntityId,
                            location.getYaw(),
                            location.getPitch(),
                            false
                    );

            WrapperPlayServerEntityHeadLook head =
                    new WrapperPlayServerEntityHeadLook(
                            mainEntityId,
                            location.getYaw()
                    );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, body);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, head);

            npc.getHologram().spawn(player);

        }), 300L);

        setCurrentLocation(location.clone());
    }

    default boolean checkEntityId(int entityID) {
        if (getSpawnedEntitiesID() == null) return false;
        for (int id : getSpawnedEntitiesID()) {
            if (id == entityID) return true;
        }
        return false;
    }

}
