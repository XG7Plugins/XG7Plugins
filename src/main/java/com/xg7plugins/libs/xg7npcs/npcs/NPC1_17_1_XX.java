package com.xg7plugins.libs.xg7npcs.npcs;


import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NPC1_17_1_XX extends NPC {

    private static final ReflectionClass entityPlayerClass = NMSUtil.getNewerNMSClass("server.level.EntityPlayer");
    private static final ReflectionClass minecraftServerClass = NMSUtil.getNewerNMSClass("server.MinecraftServer");
    private static final ReflectionClass worldServerClass = NMSUtil.getNewerNMSClass("server.level.WorldServer");
    private static final ReflectionClass entityPoseClass = NMSUtil.getNewerNMSClass("world.entity.EntityPose");
    private static final ReflectionClass packetPlayOutPlayerInfoClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutPlayerInfo");
    private static final ReflectionClass packetPlayOutScoreboardTeamClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutScoreboardTeam");
    private static final ReflectionClass enumPlayerInfoActionClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    private static final ReflectionClass packetPlayOutNamedEntitySpawnClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutNamedEntitySpawn");
    private static final ReflectionClass packetPlayOutEntityHeadRotationClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityHeadRotation");
    private static final ReflectionClass packetPlayOutEntityLookClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook");
    private static final ReflectionClass packetPlayOutEntityEquipmentClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityEquipment");
    private static final ReflectionClass scoreBoardTeamClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeam");
    private static final ReflectionClass scoreBoardClass = NMSUtil.getNewerNMSClass("world.scores.Scoreboard");
    private static final ReflectionClass enumTagVisibilityClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
    private static final ReflectionClass packetPlayOutEntityDestroyClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityDestroy");
    private static final ReflectionClass enumItemSlotClass = NMSUtil.getNewerNMSClass("world.entity.EnumItemSlot");

    public NPC1_17_1_XX(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id,name, location);
    }

    @Override
    public void spawn(Player player) {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
            try {
                PlayerNMS playerNMS = PlayerNMS.cast(player);

                ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

                ReflectionObject npc = entityPlayerClass
                        .getConstructor(
                                minecraftServerClass.getAClass(),
                                worldServerClass.getAClass(),
                                GameProfile.class
                        ).newInstance(
                            playerNMS.getCraftPlayerHandle().getMethod("getMinecraftServer").invoke(),
                            nmsWorld.getObject(),
                            skin
                        );

                npc.getMethod("setPosition", double.class, double.class, double.class)
                        .invoke(location.getX(), location.getY(), location.getZ());

                npc.getMethod("setPose", entityPoseClass.getAClass())
                        .invoke(entityPoseClass.getEnumField("STANDING"));

                ReflectionObject packetPlayOutPlayerInfo = packetPlayOutPlayerInfoClass
                        .getConstructor(
                                enumPlayerInfoActionClass.getAClass(),
                                Collection.class
                        ).newInstance(
                                enumPlayerInfoActionClass.getEnumField("ADD_PLAYER"),
                                Collections.singletonList(npc.getObject())
                        );

                ReflectionObject entitySpawn = packetPlayOutNamedEntitySpawnClass
                        .getConstructor(NMSUtil.getNewerNMSClass("world.entity.player.EntityHuman").getAClass())
                        .newInstance(npc.getObject());

                ReflectionObject team = scoreBoardTeamClass
                        .getConstructor(scoreBoardClass.getAClass(), String.class)
                        .newInstance(scoreBoardClass.newInstance().getObject(), npc.getField("displayName"));

                team.getMethod("getPlayerNameSet").invokeToRObject().getMethod("add", Object.class).invoke("dummyNPC");
                team.getMethod("setNameTagVisibility", enumTagVisibilityClass.getAClass())
                        .invoke(enumTagVisibilityClass.getEnumField("NEVER"));

                ReflectionObject scoreboardTeam = packetPlayOutScoreboardTeamClass
                        .getMethod("a", scoreBoardTeamClass.getAClass(), boolean.class)
                        .invokeToRObject(team.getObject(), true);

                float yaw = location.getYaw();
                float pitch = location.getPitch();

                ReflectionObject headRotation = packetPlayOutEntityHeadRotationClass
                        .getConstructor(NMSUtil.getNewerNMSClass("world.entity.Entity").getAClass(), byte.class)
                        .newInstance(npc.getObject(), (byte) ((yaw % 360) * 256 / 360));

                ReflectionObject bodyRotation = packetPlayOutEntityLookClass
                        .getConstructor(
                                int.class,
                                byte.class,
                                byte.class,
                                boolean.class
                        ).newInstance(
                                npc.getMethod("getId").invoke(),
                                (byte) ((yaw % 360) * 256 / 360),
                                (byte) ((pitch % 360) * 256 / 360),
                                player.isOnGround()
                        );

                ReflectionObject equipment = packetPlayOutEntityEquipmentClass
                        .getConstructor(int.class, List.class)
                        .newInstance(npc.getMethod("getId").invoke(), equipments);


                EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();
                dataWatcher.watch(17, (byte) 0x07F);

                ReflectionObject metadata = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityMetadata")
                        .getConstructor(int.class, dataWatcher.getWatcher().getObjectClass(), boolean.class)
                        .newInstance(npc.getMethod("getId").invoke(), dataWatcher.getWatcher().getObject(), true);

                playerNMS.sendPacket(packetPlayOutPlayerInfo.getObject());
                playerNMS.sendPacket(entitySpawn.getObject());
                playerNMS.sendPacket(scoreboardTeam.getObject());
                playerNMS.sendPacket(headRotation.getObject());
                playerNMS.sendPacket(bodyRotation.getObject());
                if (!equipments.isEmpty()) playerNMS.sendPacket(equipment.getObject());
                playerNMS.sendPacket(metadata.getObject());

                ReflectionObject packetPlayOutPlayerInfo2 = packetPlayOutPlayerInfoClass
                        .getConstructor(
                                enumPlayerInfoActionClass.getAClass(),
                                Collection.class
                        ).newInstance(
                                enumPlayerInfoActionClass.getEnumField("REMOVE_PLAYER"),
                                Collections.singletonList(npc.getObject())
                        );


                Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2.getObject()),20L);

                npcIDS.put(player.getUniqueId(), npc.getMethod("getId").invoke());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


    }

    @Override
    public void destroy(Player player) {
        if (!npcIDS.containsKey(player.getUniqueId())) return;

        ReflectionObject destroy = packetPlayOutEntityDestroyClass
                .getConstructor(int[].class)
                .newInstance(new int[]{npcIDS.get(player.getUniqueId())});
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(destroy.getObject());
        npcIDS.remove(player.getUniqueId());

        name.destroy(player);
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
