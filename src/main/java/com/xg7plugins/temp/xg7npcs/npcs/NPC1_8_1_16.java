package com.xg7plugins.temp.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.*;
import com.xg7plugins.utils.reflection.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class NPC1_8_1_16 extends NPC {

    private static ReflectionClass enumItemSlotClass;

    private static ReflectionClass craftWorldClass;
    private static ReflectionClass playerInteractManager;
    private static ReflectionClass worldClass;
    private static ReflectionClass worldServer;
    private static ReflectionClass entityPlayerClass;
    private static ReflectionClass minecraftClass;
    private static ReflectionClass enumPlayerInfoAction;
    private static ReflectionClass entityHumanClass;
    private static ReflectionClass entityClass;
    private static ReflectionClass scoreBoardTeamClass;
    private static ReflectionClass enumScoreboardTeamBaseEnumNameTagVisibilityClass;
    private static ReflectionClass scoreBoardClass;

    private static PacketClass packetPlayOutPlayerInfoClass;
    private static PacketClass packetPlayOutNamedEntitySpawnClass;
    private static PacketClass packetPlayOutScoreboardTeamClass;
    private static PacketClass packetPlayOutEntityHeadRotationClass;
    private static PacketClass packetPlayOutEntityLookClass;
    private static PacketClass packetPlayOutEntityMetadataClass;
    private static PacketClass packetPlayOutEntityEquipmentClass;
    private static PacketClass packetPlayOutEntityDestroyClass;

    static {
        try {
            enumItemSlotClass = XG7Plugins.getMinecraftVersion() > 9 ? NMSUtil.getNMSClass("EnumItemSlot") : null;
            craftWorldClass = NMSUtil.getCraftBukkitClass("CraftWorld");
            entityClass = NMSUtil.getNMSClass("Entity");
            playerInteractManager = NMSUtil.getNMSClass("PlayerInteractManager");
            worldClass = NMSUtil.getNMSClass("World");
            worldServer = NMSUtil.getNMSClass("WorldServer");
            entityPlayerClass = NMSUtil.getNMSClass("EntityPlayer");
            minecraftClass = NMSUtil.getNMSClass("MinecraftServer");
            enumPlayerInfoAction = NMSUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            entityHumanClass = NMSUtil.getNMSClass("EntityHuman");
            scoreBoardTeamClass = NMSUtil.getNMSClass("ScoreboardTeam");
            enumScoreboardTeamBaseEnumNameTagVisibilityClass = NMSUtil.getNMSClass("ScoreboardTeamBase$EnumNameTagVisibility");
            scoreBoardClass = NMSUtil.getNMSClass("Scoreboard");

            packetPlayOutPlayerInfoClass = new PacketClass("PacketPlayOutPlayerInfo");
            packetPlayOutNamedEntitySpawnClass = new PacketClass("PacketPlayOutNamedEntitySpawn");
            packetPlayOutScoreboardTeamClass = new PacketClass("PacketPlayOutScoreboardTeam");
            packetPlayOutEntityHeadRotationClass = new PacketClass("PacketPlayOutEntityHeadRotation");
            packetPlayOutEntityLookClass = new PacketClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
            packetPlayOutEntityMetadataClass = new PacketClass("PacketPlayOutEntityMetadata");
            packetPlayOutEntityEquipmentClass = new PacketClass("PacketPlayOutEntityEquipment");
            packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");
        } catch (Exception ignored) {

        }

    }

    public NPC1_8_1_16(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id, name, location);
    }

    @Override
    public void spawn(Player player) {


        PlayerNMS playerNMS = PlayerNMS.cast(player);

        ReflectionObject nmsWorld = craftWorldClass.castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

        ReflectionObject interactManager = playerInteractManager
                .getConstructor(XG7Plugins.getMinecraftVersion() > 13 ? worldServer.getAClass() : worldClass.getAClass())
                .newInstance(nmsWorld.getObject());

        GameProfile npcSkin = (GameProfile) skin;

        if (playerSkin) {
            GameProfile playerGameProfile = playerNMS.getCraftPlayer().getMethod("getProfile").invoke();

            npcSkin = new GameProfile(UUID.randomUUID(), "dummyNPC");

            npcSkin.getProperties().putAll(playerGameProfile.getProperties());

        }

        ReflectionObject npc = entityPlayerClass
                .getConstructor(minecraftClass.getAClass(), worldServer.getAClass(), GameProfile.class, playerInteractManager.getAClass())
                .newInstance(playerNMS.getCraftPlayerHandle().getField("server"), nmsWorld.getObject(), npcSkin, interactManager.getObject());

        npc.getMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
                .invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        Packet packetPlayOutPlayerInfo = new Packet(packetPlayOutPlayerInfoClass, new Class<?>[]{enumPlayerInfoAction.getAClass(), Iterable.class}, enumPlayerInfoAction.getEnumField("ADD_PLAYER"), Collections.singletonList(npc.getObject()));
        Packet packetPlayOutNamedEntitySpawn = new Packet(packetPlayOutNamedEntitySpawnClass, new Class<?>[]{entityHumanClass.getAClass()}, npc.getObject());

        ReflectionObject team = scoreBoardTeamClass.getConstructor(scoreBoardClass.getAClass(), String.class)
                .newInstance(scoreBoardClass.newInstance().getObject(), npc.getField("displayName"));

        team.getMethod("getPlayerNameSet").invokeToRObject().getMethod("add", Object.class).invoke("dummyNPC");

        team.getMethod("setNameTagVisibility", enumScoreboardTeamBaseEnumNameTagVisibilityClass.getAClass())
                .invoke(enumScoreboardTeamBaseEnumNameTagVisibilityClass.getEnumField("NEVER"));

        Packet packetPlayOutScoreBoardTeam = new Packet(packetPlayOutScoreboardTeamClass, team.getObject(),0);

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Packet packetPlayOutEntityHeadRotation = new Packet(packetPlayOutEntityHeadRotationClass, new Class<?>[]{entityClass.getAClass(), byte.class}, npc.getObject(), (byte) ((yaw % 360) * 256 / 360));
        Packet packetPlayOutEntityLook = new Packet(packetPlayOutEntityLookClass, (int) npc.getMethod("getId").invoke(), (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());

        EntityDataWatcher dataWatcher = new EntityDataWatcher();

        if (XG7Plugins.getMinecraftVersion() > 7) dataWatcher.watch(XG7Plugins.getMinecraftVersion() < 9 ? 10 : XG7Plugins.getMinecraftVersion() < 13 ? 16 : 17, XG7Plugins.getMinecraftVersion() > 11 ? (byte) 0x7F : (byte) 0xFF);

        Packet packetPlayOutEntityMetadata = new Packet(packetPlayOutEntityMetadataClass);

        packetPlayOutEntityMetadata.setField("a", npc.getMethod("getId").invoke());
        packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcher().getMethod("c").invoke());

        Packet packetPlayOutPlayerInfo2 = new Packet(packetPlayOutPlayerInfoClass, new Class<?>[]{enumPlayerInfoAction.getAClass(), Iterable.class}, enumPlayerInfoAction.getEnumField("REMOVE_PLAYER"), Collections.singletonList(npc.getObject()));

        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {

            playerNMS.sendPacket(packetPlayOutPlayerInfo);
            playerNMS.sendPacket(packetPlayOutNamedEntitySpawn);
            playerNMS.sendPacket(packetPlayOutScoreBoardTeam);
            playerNMS.sendPacket(packetPlayOutEntityHeadRotation);
            playerNMS.sendPacket(packetPlayOutEntityLook);
            if (XG7Plugins.getMinecraftVersion() >= 8) playerNMS.sendPacket(packetPlayOutEntityMetadata);

            if (!equipments.isEmpty()) {
                if (XG7Plugins.getMinecraftVersion() == 16) {

                    Packet packetPlayOutEntityEquipment = new Packet(packetPlayOutEntityEquipmentClass, (int) npc.getMethod("getId").invoke(), Pair.toMojangPairList(equipments));

                    playerNMS.sendPacket(packetPlayOutEntityEquipment);
                } else {
                    for (Pair<?, ?> pair : equipments) {

                        Packet packetPlayOutEntityEquipment = new Packet(packetPlayOutEntityEquipmentClass, (int) npc.getMethod("getId").invoke(), pair.getFirst(), pair.getSecond());

                        playerNMS.sendPacket(packetPlayOutEntityEquipment);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2), 20L);
        });

        npcIDS.put(player.getUniqueId(), npc.getMethod("getId").invoke());
        if (lookAtPlayer)
            XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npc.getMethod("getId").invoke(), npc.getObject());


    }


    @Override
    public void destroy(Player player) {
        if (!npcIDS.containsKey(player.getUniqueId())) return;

        Packet packetPlayOutEntityDestroy = new Packet(packetPlayOutEntityDestroyClass, new Class[]{int[].class}, new int[]{npcIDS.get(player.getUniqueId())});
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packetPlayOutEntityDestroy);
        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().unregisterLookingNPC(npcIDS.get(player.getUniqueId()));

        npcIDS.remove(player.getUniqueId());

        ((Hologram)name).destroy(player);
    }

    public void lookAtPlayer(Player player, int id, Object npc) {

            Location playerLocation = Location.fromPlayer(player);

            double deltaX = playerLocation.getX() - location.getX();
            double deltaY = playerLocation.getY() - location.getY();
            double deltaZ = playerLocation.getZ() - location.getZ();

            double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            float pitch = (float) Math.toDegrees(-Math.atan(deltaY / distanceXZ));


        Packet packetPlayOutEntityHeadRotation = new Packet(packetPlayOutEntityHeadRotationClass, npc, (byte) ((yaw % 360) * 256 / 360));
        Packet packetPlayOutEntityLook = new Packet(packetPlayOutEntityLookClass, id, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());


        PlayerNMS nms = PlayerNMS.cast(player);

            nms.sendPacket(packetPlayOutEntityHeadRotation);
            nms.sendPacket(packetPlayOutEntityLook);



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

            if (mainHandNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("MAINHAND") : 0, mainHandNMS));
            if (offHandNMS != null && XG7Plugins.getMinecraftVersion() > 8) equipmentList.add(new Pair<>(enumItemSlotClass.getEnumField("OFFHAND"), offHandNMS));
            if (helmetNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("HEAD") : 4, helmetNMS));
            if (chestplateNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("CHEST") : 3, chestplateNMS));
            if (leggingsNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("LEGS") : 2, leggingsNMS));
            if (bootsNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("FEET") : 1, bootsNMS));


            this.equipments = equipmentList;

            Bukkit.getOnlinePlayers().forEach(this::destroy);


    }
}
