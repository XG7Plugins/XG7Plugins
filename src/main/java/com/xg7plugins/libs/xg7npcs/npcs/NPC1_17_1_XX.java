package com.xg7plugins.libs.xg7npcs.npcs;


import com.mojang.authlib.GameProfile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.Pair;
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
    private static final ReflectionClass packetPlayOutPlayerInfoClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutPlayerInfo");
    private static final ReflectionClass packetPlayOutScoreboardTeamClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutScoreboardTeam");
    private static final ReflectionClass enumPlayerInfoActionClass = XG7Plugins.getMinecraftVersion() >= 19 ? null : ReflectionClass.of(packetPlayOutPlayerInfoClass.getClassInside("EnumPlayerInfoAction"));
    private static final ReflectionClass packetPlayOutNamedEntitySpawnClass = XG7Plugins.getMinecraftVersion() > 20 ? null : NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutNamedEntitySpawn");
    private static final ReflectionClass packetPlayOutEntityHeadRotationClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityHeadRotation");
    private static final ReflectionClass packetPlayOutEntityLookClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook");
    private static final ReflectionClass packetPlayOutEntityEquipmentClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityEquipment");
    private static final ReflectionClass scoreBoardTeamClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeam");
    private static final ReflectionClass scoreBoardClass = NMSUtil.getNewerNMSClass("world.scores.Scoreboard");
    private static final ReflectionClass enumTagVisibilityClass = NMSUtil.getNewerNMSClass("world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
    private static final ReflectionClass packetPlayOutEntityDestroyClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityDestroy");
    private static final ReflectionClass enumItemSlotClass = NMSUtil.getNewerNMSClass("world.entity.EnumItemSlot");
    private static final ReflectionClass clientBoundPlayerInfoUpdateClass = XG7Plugins.getMinecraftVersion() < 19 ? null : NMSUtil.getNewerNMSClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket");
    private static final ReflectionClass clientBoundPlayerInfoRemoveClass = XG7Plugins.getMinecraftVersion() < 19 ? null : NMSUtil.getNewerNMSClass("network.protocol.game.ClientboundPlayerInfoRemovePacket");
    private static final ReflectionClass enumPlayerInfoActionClassNewer = XG7Plugins.getMinecraftVersion() < 19 ? null : ReflectionClass.of(clientBoundPlayerInfoUpdateClass.getClassInside("a"));
    private static final ReflectionClass packetPlayOutSpawnEntityClass = XG7Plugins.getMinecraftVersion() < 21 ? null : NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutSpawnEntity");
    private static final ReflectionClass entityClass = NMSUtil.getNewerNMSClass("world.entity.Entity");
    private static final ReflectionClass blockPositionClass = NMSUtil.getNewerNMSClass("core.BlockPosition");
    private static final ReflectionClass entityTPClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityTeleport");
    private static final ReflectionClass packetPlayOutEntityMetadataClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityMetadata");


    public NPC1_17_1_XX(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id,name, location);
    }

    @Override
    public void spawn(Player player) {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
                PlayerNMS playerNMS = PlayerNMS.cast(player);

                ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

                ReflectionObject npc;

            GameProfile npcSkin = skin;

            if (playerSkin) {
                GameProfile playerGameProfile = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player).getMethod("getProfile").invoke();

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
                            NMSUtil.getNewerNMSClass("server.level.ClientInformation").getAClass()
                    ).newInstance(
                            playerNMS.getCraftPlayerHandle().getField("d"),
                            nmsWorld.getObject(),
                            npcSkin,
                            NMSUtil.getNewerNMSClass("server.level.ClientInformation").getMethod("a").invoke()
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


            ReflectionObject packetPlayOutPlayerInfo = XG7Plugins.getMinecraftVersion() < 19 ?
                        packetPlayOutPlayerInfoClass
                        .getConstructor(
                                enumPlayerInfoActionClass.getAClass(),
                                Collection.class
                        ).newInstance(
                                enumPlayerInfoActionClass.getEnumField("ADD_PLAYER"),
                                Collections.singletonList(npc.getObject())
                        )
                        :
                        clientBoundPlayerInfoUpdateClass
                                .getConstructor(
                                        enumPlayerInfoActionClassNewer.getAClass(),
                                        entityPlayerClass.getAClass()
                                ).newInstance(
                                        enumPlayerInfoActionClassNewer.getEnumField("ADD_PLAYER"),
                                        npc.getObject()
                        );

                ReflectionObject entitySpawn = XG7Plugins.getMinecraftVersion() < 21 ?
                        packetPlayOutNamedEntitySpawnClass
                        .getConstructor(NMSUtil.getNewerNMSClass("world.entity.player.EntityHuman").getAClass())
                        .newInstance(npc.getObject())
                        :
                        packetPlayOutSpawnEntityClass
                                .getConstructor(
                                        entityClass.getAClass(),
                                        int.class,
                                        blockPositionClass.getAClass()
                                )
                                .newInstance(
                                        npc.getObject(),
                                        0,
                                        blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance((int) location.getX(), (int) location.getY(), (int) location.getZ()).getObject()
                                );

                ReflectionObject packetTeleport = entityTPClass
                        .getConstructor(entityClass.getAClass())
                        .newInstance(npc.getObject());

                ReflectionObject team = scoreBoardTeamClass
                        .getConstructor(scoreBoardClass.getAClass(), String.class)
                        .newInstance(scoreBoardClass.newInstance().getObject(), "dummynpc");

                team.getMethod(XG7Plugins.getMinecraftVersion() < 19 ? "getPlayerNameSet" : "g").invokeToRObject().getMethod("add", Object.class).invoke("dummyNPC");
                team.getMethod(XG7Plugins.getMinecraftVersion() < 19 ? "setNameTagVisibility" : "a", enumTagVisibilityClass.getAClass())
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
                                npcId,
                                (byte) ((yaw % 360) * 256 / 360),
                                (byte) ((pitch % 360) * 256 / 360),
                                player.isOnGround()
                        );

                ReflectionObject equipment = packetPlayOutEntityEquipmentClass
                        .getConstructor(int.class, List.class)
                        .newInstance(npcId, Pair.toMojangPairList(equipments));


                EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();
                dataWatcher.watch(17, (byte) 0x07F);

            ReflectionObject metadata = XG7Plugins.getMinecraftVersion() < 19 ?
                    packetPlayOutEntityMetadataClass
                            .getConstructor(int.class, dataWatcher.getWatcher().getObjectClass(), boolean.class)
                            .newInstance(npcId, dataWatcher.getWatcher().getObject(), true)
                    :
                    packetPlayOutEntityMetadataClass
                            .getConstructor(int.class, List.class)
                            .newInstance(npcId, dataWatcher.getWatcher().getMethod("b").invoke());

                playerNMS.sendPacket(packetPlayOutPlayerInfo.getObject());
                playerNMS.sendPacket(entitySpawn.getObject());
                playerNMS.sendPacket(packetTeleport.getObject());
                playerNMS.sendPacket(scoreboardTeam.getObject());
                playerNMS.sendPacket(headRotation.getObject());
                playerNMS.sendPacket(bodyRotation.getObject());
                if (!equipments.isEmpty()) playerNMS.sendPacket(equipment.getObject());
                playerNMS.sendPacket(metadata.getObject());

            ReflectionObject packetPlayOutPlayerInfo2 = XG7Plugins.getMinecraftVersion() < 19 ?
                    packetPlayOutPlayerInfoClass
                            .getConstructor(
                                    enumPlayerInfoActionClass.getAClass(),
                                    Collection.class
                            ).newInstance(
                                    enumPlayerInfoActionClass.getEnumField("REMOVE_PLAYER"),
                                    Collections.singletonList(npc.getObject())
                            )
                    :
                    clientBoundPlayerInfoRemoveClass
                            .getConstructor(List.class)
                            .newInstance(Collections.singletonList(npc.getFieldFromSuperClass(entityClass.getAClass(), "ax")));

                Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2.getObject()),20L);

                npcIDS.put(player.getUniqueId(), npcId);

                if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npcId, npc.getObject());

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
        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().unregisterLookingNPC(npcIDS.get(player.getUniqueId()));

        name.destroy(player);
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

        ReflectionObject headRotation = packetPlayOutEntityHeadRotationClass
                .getConstructor(NMSUtil.getNewerNMSClass("world.entity.Entity").getAClass(), byte.class)
                .newInstance(npc, (byte) ((yaw % 360) * 256 / 360));

        ReflectionObject bodyRotation = packetPlayOutEntityLookClass
                .getConstructor(
                        int.class,
                        byte.class,
                        byte.class,
                        boolean.class
                ).newInstance(
                        id,
                        (byte) ((yaw % 360) * 256 / 360),
                        (byte) ((pitch % 360) * 256 / 360),
                        player.isOnGround()
                );


        PlayerNMS nms = PlayerNMS.cast(player);

        nms.sendPacket(headRotation.getObject());
        nms.sendPacket(bodyRotation.getObject());

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
