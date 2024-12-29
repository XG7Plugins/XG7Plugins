package com.xg7plugins.libs.xg7npcs.npcs;


import com.mojang.authlib.GameProfile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NPC1_17_1_XX extends NPC {

    private static PacketClass packetPlayOutPlayerInfoClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : new PacketClass("PacketPlayOutPlayerInfo");
    private static PacketClass packetPlayOutScoreboardTeamClass = new PacketClass("PacketPlayOutScoreboardTeam");
    private static PacketClass packetPlayOutNamedEntitySpawnClass = XG7Plugins.getMinecraftVersion() > 20 ? null : new PacketClass("PacketPlayOutNamedEntitySpawn");
    private static PacketClass packetPlayOutEntityHeadRotationClass = new PacketClass("PacketPlayOutEntityHeadRotation");
    private static PacketClass packetPlayOutEntityLookClass = new PacketClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
    private static PacketClass packetPlayOutEntityEquipmentClass = new PacketClass("PacketPlayOutEntityEquipment");
    private static PacketClass packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");
    private static PacketClass clientBoundPlayerInfoUpdateClass = XG7Plugins.getMinecraftVersion() < 19 ? null : new PacketClass("ClientboundPlayerInfoUpdatePacket");
    private static PacketClass clientBoundPlayerInfoRemoveClass = XG7Plugins.getMinecraftVersion() < 19 ? null : new PacketClass("ClientboundPlayerInfoRemovePacket");
    private static PacketClass packetPlayOutSpawnEntityClass = XG7Plugins.getMinecraftVersion() < 21 ? null : new PacketClass("PacketPlayOutSpawnEntity");
    private static PacketClass entityTPClass = new PacketClass("PacketPlayOutEntityTeleport");
    private static PacketClass packetPlayOutEntityMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");

    private static ReflectionClass craftWorldClass = NMSUtil.getCraftBukkitClass("CraftWorld");
    private static ReflectionClass entityPlayerClass = NMSUtil.getNewerNMSClass("server.level.EntityPlayer");
    private static ReflectionClass minecraftServerClass = NMSUtil.getNewerNMSClass("server.MinecraftServer");
    private static ReflectionClass worldServerClass = NMSUtil.getNewerNMSClass("server.level.WorldServer");
    private static ReflectionClass entityPoseClass = NMSUtil.getNewerNMSClass("world.entity.EntityPose");
    private static ReflectionClass enumPlayerInfoActionClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : ReflectionClass.of(packetPlayOutPlayerInfoClass.getReflectionClass().getClassInside("EnumPlayerInfoAction"));
    private static ReflectionClass scoreBoardTeamClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeam");
    private static ReflectionClass scoreBoardClass = NMSUtil.getNewerNMSClass("world.scores.Scoreboard");
    private static ReflectionClass enumTagVisibilityClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
    private static ReflectionClass enumItemSlotClass = NMSUtil.getNewerNMSClass("world.entity.EnumItemSlot");
    private static ReflectionClass entityClass = NMSUtil.getNewerNMSClass("world.entity.Entity");
    private static ReflectionClass blockPositionClass = NMSUtil.getNewerNMSClass("core.BlockPosition");
    private static ReflectionClass clientInfoClass = NMSUtil.getNewerNMSClass("server.level.ClientInformation");
    private static ReflectionClass entityHumanClass = NMSUtil.getNewerNMSClass("world.entity.player.EntityHuman");
    private static ReflectionClass enumPlayerInfoActionClassNewer = XG7Plugins.getMinecraftVersion() < 19 ? null : ReflectionClass.of(clientBoundPlayerInfoUpdateClass.getReflectionClass().getClassInside("a"));

    static {
        try {
            packetPlayOutPlayerInfoClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : new PacketClass("PacketPlayOutPlayerInfo");
            packetPlayOutScoreboardTeamClass = new PacketClass("PacketPlayOutScoreboardTeam");
            packetPlayOutNamedEntitySpawnClass = XG7Plugins.getMinecraftVersion() > 20 ? null : new PacketClass("PacketPlayOutNamedEntitySpawn");
            packetPlayOutEntityHeadRotationClass = new PacketClass("PacketPlayOutEntityHeadRotation");
            packetPlayOutEntityLookClass = new PacketClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
            packetPlayOutEntityEquipmentClass = new PacketClass("PacketPlayOutEntityEquipment");
            packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");
            clientBoundPlayerInfoUpdateClass = XG7Plugins.getMinecraftVersion() < 19 ? null : new PacketClass("ClientboundPlayerInfoUpdatePacket");
            clientBoundPlayerInfoRemoveClass = XG7Plugins.getMinecraftVersion() < 19 ? null : new PacketClass("ClientboundPlayerInfoRemovePacket");
            packetPlayOutSpawnEntityClass = XG7Plugins.getMinecraftVersion() < 21 ? null : new PacketClass("PacketPlayOutSpawnEntity");
            entityTPClass = new PacketClass("PacketPlayOutEntityTeleport");
            packetPlayOutEntityMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");

            craftWorldClass = NMSUtil.getCraftBukkitClass("CraftWorld");
            entityPlayerClass = NMSUtil.getNewerNMSClass("server.level.EntityPlayer");
            minecraftServerClass = NMSUtil.getNewerNMSClass("server.MinecraftServer");
            worldServerClass = NMSUtil.getNewerNMSClass("server.level.WorldServer");
            entityPoseClass = NMSUtil.getNewerNMSClass("world.entity.EntityPose");
            enumPlayerInfoActionClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : ReflectionClass.of(packetPlayOutPlayerInfoClass.getReflectionClass().getClassInside("EnumPlayerInfoAction"));
            scoreBoardTeamClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeam");
            scoreBoardClass = NMSUtil.getNewerNMSClass("world.scores.Scoreboard");
            enumTagVisibilityClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
            enumItemSlotClass = NMSUtil.getNewerNMSClass("world.entity.EnumItemSlot");
            entityClass = NMSUtil.getNewerNMSClass("world.entity.Entity");
            blockPositionClass = NMSUtil.getNewerNMSClass("core.BlockPosition");
            clientInfoClass = NMSUtil.getNewerNMSClass("server.level.ClientInformation");
            entityHumanClass = NMSUtil.getNewerNMSClass("world.entity.player.EntityHuman");
            enumPlayerInfoActionClassNewer = XG7Plugins.getMinecraftVersion() < 19 ? null : ReflectionClass.of(clientBoundPlayerInfoUpdateClass.getReflectionClass().getClassInside("a"));
        } catch (Exception ignored) {
        }
    }

    public NPC1_17_1_XX(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id,name, location);
    }

    @Override
    public void spawn(Player player) {

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        ReflectionObject nmsWorld = craftWorldClass.castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

        ReflectionObject npc;

        GameProfile npcSkin = (GameProfile) skin;

        if (playerSkin) {
            GameProfile playerGameProfile = playerNMS.getCraftPlayer().getMethod("getProfile").invoke();

            npcSkin = new GameProfile(UUID.randomUUID(), "dummyNPC");

            npcSkin.getProperties().putAll(playerGameProfile.getProperties());

        }

        if (XG7Plugins.getMinecraftVersion() < 21) {
            npc = entityPlayerClass
                    .getConstructor(
                            minecraftServerClass.getAClass(),
                            worldServerClass.getAClass(),
                            GameProfile.class
                    ).newInstance(
                            XG7Plugins.getMinecraftVersion() < 19 ? playerNMS.getCraftPlayerHandle().getMethod("getMinecraftServer").invoke() : playerNMS.getCraftPlayerHandle().getField("d"),
                            nmsWorld.getObject(),
                            npcSkin
                    );
        } else {
            npc = entityPlayerClass.getConstructor(
                    minecraftServerClass.getAClass(),
                    worldServerClass.getAClass(),
                    GameProfile.class,
                    clientInfoClass.getAClass()
            ).newInstance(
                    playerNMS.getCraftPlayerHandle().getField("d"),
                    nmsWorld.getObject(),
                    npcSkin,
                    clientInfoClass.getMethod("a").invoke()
            );
        }

        npc.getMethod(XG7Plugins.getMinecraftVersion() > 19 ? "b" : "setPositionRotation", double.class, double.class, double.class, float.class, float.class)
                .invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        npc.getMethod(XG7Plugins.getMinecraftVersion() > 19 ? "c" : "setPose", entityPoseClass.getAClass())
                .invoke(entityPoseClass.getEnumField("STANDING"));

        if (XG7Plugins.getMinecraftVersion() >= 21) {
            npc.setField("c", playerNMS.getPlayerConnection().getObject());
        }

        int npcId = XG7Plugins.getMinecraftVersion() < 19 ? npc.getMethod("getId").invoke() : npc.getFieldFromSuperClass(entityClass.getAClass(), XG7Plugins.getMinecraftVersion() < 21 ? "q" : "o");

        Packet packetPlayOutPlayerInfo = XG7Plugins.getMinecraftVersion() < 19 ?
                new Packet(packetPlayOutPlayerInfoClass, new Class<?>[]{enumPlayerInfoActionClass.getAClass(), Collection.class}, enumPlayerInfoActionClass.getEnumField("ADD_PLAYER"), Collections.singletonList(npc.getObject()))
                :
                new Packet(clientBoundPlayerInfoUpdateClass, enumPlayerInfoActionClassNewer.getEnumField("ADD_PLAYER"), npc.getObject());

        Packet packetEntitySpawn = XG7Plugins.getMinecraftVersion() < 21 ?
                new Packet(packetPlayOutNamedEntitySpawnClass, new Class<?>[]{entityHumanClass.getAClass()}, npc.getObject())
                :
                new Packet(packetPlayOutSpawnEntityClass, new Class<?>[]{entityClass.getAClass(), int.class, blockPositionClass.getAClass()}, npc.getObject(), 0, blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance((int) location.getX(), (int) location.getY(), (int) location.getZ()).getObject());


        Packet packetTeleport = new Packet(
                entityTPClass, new Class<?>[]{entityClass.getAClass()}, npc.getObject()
        );

        ReflectionObject team = scoreBoardTeamClass
                .getConstructor(scoreBoardClass.getAClass(), String.class)
                .newInstance(scoreBoardClass.newInstance().getObject(), "dummynpc");

        team.getMethod(XG7Plugins.getMinecraftVersion() < 19 ? "getPlayerNameSet" : "g").invokeToRObject().getMethod("add", Object.class).invoke("dummyNPC");
        team.getMethod(XG7Plugins.getMinecraftVersion() < 19 ? "setNameTagVisibility" : "a", enumTagVisibilityClass.getAClass())
                .invoke(enumTagVisibilityClass.getEnumField("NEVER"));


        Packet packetScoreBoardTeam = new Packet(
                packetPlayOutScoreboardTeamClass.getReflectionClass()
                        .getMethod("a", scoreBoardTeamClass.getAClass(), boolean.class)
                        .invoke(team.getObject(), true)
        );

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Packet packetHeadRotation = new Packet(
                packetPlayOutEntityHeadRotationClass,
                new Class<?>[]{entityClass.getAClass(), byte.class},
                npc.getObject(), (byte) ((yaw % 360) * 256 / 360)
        );

        Packet packetBodyRotation = new Packet(packetPlayOutEntityLookClass, npcId, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());

        Packet packetEquipment = new Packet(
                packetPlayOutEntityEquipmentClass,
                npcId, Pair.toMojangPairList(equipments)
        );


        EntityDataWatcher dataWatcher = new EntityDataWatcher();
        dataWatcher.watch(17, (byte) 0x07F);

        Packet packetMetadata = XG7Plugins.getMinecraftVersion() < 19 ?
                new Packet(packetPlayOutEntityMetadataClass,
                        npcId,
                        dataWatcher.getWatcher().getObject(),
                        true
                ) :
                new Packet(packetPlayOutEntityMetadataClass,
                        npcId,
                        dataWatcher.getWatcher().getMethod("b").invoke()
                );

        Packet packetPlayOutPlayerInfo2 = XG7Plugins.getMinecraftVersion() < 19 ?
                new Packet(packetPlayOutPlayerInfoClass, new Class<?>[]{enumPlayerInfoActionClass.getAClass(), Collection.class}, enumPlayerInfoActionClass.getEnumField("REMOVE_PLAYER"), Collections.singletonList(npc.getObject()))
                :
                new Packet(clientBoundPlayerInfoRemoveClass, new Class<?>[]{List.class}, Collections.singletonList(npc.getFieldFromSuperClass(entityClass.getAClass(), "ax")));

        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {

            playerNMS.sendPacket(packetPlayOutPlayerInfo);
            playerNMS.sendPacket(packetEntitySpawn);
            playerNMS.sendPacket(packetTeleport);
            playerNMS.sendPacket(packetScoreBoardTeam);
            playerNMS.sendPacket(packetHeadRotation);
            playerNMS.sendPacket(packetBodyRotation);
            if (!equipments.isEmpty()) playerNMS.sendPacket(packetEquipment);
            playerNMS.sendPacket(packetMetadata);

            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2), 20L);
        });

        npcIDS.put(player.getUniqueId(), npcId);

        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npcId, npc.getObject());


    }

    @Override
    public void destroy(Player player) {
        if (!npcIDS.containsKey(player.getUniqueId())) return;

        Packet packetPlayOutEntityDestroy = new Packet(packetPlayOutEntityDestroyClass, new int[]{npcIDS.get(player.getUniqueId())});

        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packetPlayOutEntityDestroy);
        npcIDS.remove(player.getUniqueId());
        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().unregisterLookingNPC(npcIDS.get(player.getUniqueId()));

        if (name instanceof Hologram) ((Hologram)name).destroy(player);
    }

    @Override
    public void lookAtPlayer(Player player, int id, Object npc) {

        Location playerLocation = Location.fromPlayer(player);

        double deltaX = playerLocation.getX() - location.getX();
        double deltaY = playerLocation.getY() - location.getY();
        double deltaZ = playerLocation.getZ() - location.getZ();

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan(deltaY / distanceXZ));

        Packet packetHeadRotation = new Packet(
                packetPlayOutEntityHeadRotationClass,
                new Class<?>[]{entityClass.getAClass(), byte.class},
                npc, (byte) ((yaw % 360) * 256 / 360)
        );

        Packet packetBodyRotation = new Packet(packetPlayOutEntityLookClass, id, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());

        PlayerNMS nms = PlayerNMS.cast(player);

        nms.sendPacket(packetHeadRotation);
        nms.sendPacket(packetBodyRotation);

    }

    @Override
    public void setEquipment(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {


            Object mainHandNMS = mainHand == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(mainHand);
            Object offHandNMS = offHand == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(offHand);
            Object helmetNMS = helmet == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(helmet);
            Object chestplateNMS = chestplate == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(chestplate);
            Object leggingsNMS = leggings == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(leggings);
            Object bootsNMS = boots == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(boots);

            List<Pair<?, ?>> equipmentList = new ArrayList<>();

            if (mainHandNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("MAINHAND"), mainHandNMS));
            if (offHandNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("OFFHAND"), offHandNMS));
            if (helmetNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("HEAD"), helmetNMS));
            if (chestplateNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("CHEST"), chestplateNMS));
            if (leggingsNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("LEGS"), leggingsNMS));
            if (bootsNMS != null) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("FEET"), bootsNMS));

            this.equipments = equipmentList;

            Bukkit.getOnlinePlayers().forEach(this::destroy);



    }



}
