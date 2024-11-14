package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class NPC1_8_1_16 extends NPC {

    private static final ReflectionClass enumItemSlotClass = XG7Plugins.getMinecraftVersion() > 9 ?NMSUtil.getNMSClass("EnumItemSlot") : null;

    public NPC1_8_1_16(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id, name, location);
    }

    @Override
    public void spawn(Player player) {

        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
            PlayerNMS playerNMS = PlayerNMS.cast(player);

            ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

            ReflectionObject interactManager = NMSUtil.getNMSClass("PlayerInteractManager")
                    .getConstructor(NMSUtil.getNMSClass(XG7Plugins.getMinecraftVersion() > 13 ? "WorldServer" : "World").getAClass())
                    .newInstance(nmsWorld.getObject());

            GameProfile npcSkin = skin;

            if (playerSkin) {
                GameProfile playerGameProfile = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player).getMethod("getProfile").invoke();

                npcSkin = new GameProfile(UUID.randomUUID(), "dummyNPC");

                npcSkin.getProperties().putAll(playerGameProfile.getProperties());

            }

            ReflectionObject npc = NMSUtil.getNMSClass("EntityPlayer")
                    .getConstructor(NMSUtil.getNMSClass("MinecraftServer").getAClass(), NMSUtil.getNMSClass("WorldServer").getAClass(), GameProfile.class, NMSUtil.getNMSClass("PlayerInteractManager").getAClass())
                    .newInstance(playerNMS.getCraftPlayerHandle().getField("server"), nmsWorld.getObject(), npcSkin, interactManager.getObject());

            npc.getMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            ReflectionObject packetPlayOutPlayerInfo = NMSUtil.getNMSClass("PacketPlayOutPlayerInfo")
                    .getConstructor(NMSUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getAClass(),
                            Iterable.class)
                    .newInstance(NMSUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumField("ADD_PLAYER"), Collections.singletonList(npc.getObject()));

            ReflectionObject packetPlayOutNamedEntitySpawn = NMSUtil.getNMSClass("PacketPlayOutNamedEntitySpawn")
                    .getConstructor(NMSUtil.getNMSClass("EntityHuman").getAClass())
                    .newInstance(npc.getObject());

            ReflectionObject team = NMSUtil.getNMSClass("ScoreboardTeam")
                    .getConstructor(NMSUtil.getNMSClass("Scoreboard").getAClass(), String.class)
                    .newInstance(NMSUtil.getNMSClass("Scoreboard").newInstance().getObject(), npc.getField("displayName"));

            team.getMethod("getPlayerNameSet").invokeToRObject().getMethod("add", Object.class).invoke("dummyNPC");

            team.getMethod("setNameTagVisibility", NMSUtil.getNMSClass("ScoreboardTeamBase$EnumNameTagVisibility").getAClass())
                    .invoke(NMSUtil.getNMSClass("ScoreboardTeamBase$EnumNameTagVisibility").getEnumField("NEVER"));

            ReflectionObject packetPlayOutScoreboardTeam = NMSUtil.getNMSClass("PacketPlayOutScoreboardTeam")
                    .getConstructor(NMSUtil.getNMSClass("ScoreboardTeam").getAClass(), int.class)
                    .newInstance(team.getObject(), 0);

            float yaw = location.getYaw();
            float pitch = location.getPitch();

            ReflectionObject packetPlayOutEntityHeadRotation = NMSUtil.getNMSClass("PacketPlayOutEntityHeadRotation")
                    .getConstructor(NMSUtil.getNMSClass("Entity").getAClass(), byte.class)
                    .newInstance(npc.getObject(), (byte) ((yaw % 360) * 256 / 360));

            ReflectionObject packetPlayOutEntityLook = NMSUtil.getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook")
                    .getConstructor(int.class, byte.class, byte.class, boolean.class)
                    .newInstance(npc.getMethod("getId").invoke(), (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            if (XG7Plugins.getMinecraftVersion() > 7)
                dataWatcher.watch(XG7Plugins.getMinecraftVersion() < 9 ? 10 : XG7Plugins.getMinecraftVersion() < 13 ? 16 : 17, XG7Plugins.getMinecraftVersion() > 11 ? (byte) 0x7F : (byte) 0xFF);

            ReflectionObject packetPlayOutEntityMetadata = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata").newInstance();
            packetPlayOutEntityMetadata.setField("a", npc.getMethod("getId").invoke());
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getWatcherAsARObject().getMethod("c").invoke());

            playerNMS.sendPacket(packetPlayOutPlayerInfo.getObject());
            playerNMS.sendPacket(packetPlayOutNamedEntitySpawn.getObject());
            playerNMS.sendPacket(packetPlayOutScoreboardTeam.getObject());
            playerNMS.sendPacket(packetPlayOutEntityHeadRotation.getObject());
            playerNMS.sendPacket(packetPlayOutEntityLook.getObject());
            if (XG7Plugins.getMinecraftVersion() >= 8) playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());

            if (!equipments.isEmpty()) {
                if (XG7Plugins.getMinecraftVersion() == 16) {
                    ReflectionObject packetPlayOutEntityEquipment = NMSUtil.getNMSClass("PacketPlayOutEntityEquipment")
                            .getConstructor(int.class, List.class)
                            .newInstance(npc.getMethod("getId").invoke(), Pair.toMojangPairList(equipments));
                    playerNMS.sendPacket(packetPlayOutEntityEquipment.getObject());
                } else {
                    for (Pair<?, ?> pair : equipments) {
                        ReflectionObject packetPlayOutEntityEquipment = NMSUtil.getNMSClass("PacketPlayOutEntityEquipment")
                                .getConstructor(int.class, XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getAClass() : int.class, NMSUtil.getNMSClass("ItemStack").getAClass())
                                .newInstance(npc.getMethod("getId").invoke(), pair.getFirst(), pair.getSecond());
                        playerNMS.sendPacket(packetPlayOutEntityEquipment.getObject());
                    }
                }
            }


            ReflectionObject packetPlayOutPlayerInfo2 = NMSUtil.getNMSClass("PacketPlayOutPlayerInfo")
                    .getConstructor(NMSUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getAClass(),
                            Iterable.class)
                    .newInstance(NMSUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumField("REMOVE_PLAYER"), Collections.singletonList(npc.getObject()));

            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2.getObject()), 20L);

            npcIDS.put(player.getUniqueId(), npc.getMethod("getId").invoke());
            if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npc.getMethod("getId").invoke(), npc.getObject());

        });



    }


    @Override
    public void destroy(Player player) {
        if (!npcIDS.containsKey(player.getUniqueId())) return;

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy")
                .getConstructor(int[].class)
                .newInstance(new int[]{npcIDS.get(player.getUniqueId())});
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packet.getObject());
        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().unregisterLookingNPC(npcIDS.get(player.getUniqueId()));

        npcIDS.remove(player.getUniqueId());

        name.destroy(player);
    }

    public void lookAtPlayer(Player player, int id, Object npc) {

            Location playerLocation = Location.fromPlayer(player);

            double deltaX = playerLocation.getX() - location.getX();
            double deltaY = playerLocation.getY() - location.getY();
            double deltaZ = playerLocation.getZ() - location.getZ();

            double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            float pitch = (float) Math.toDegrees(-Math.atan(deltaY / distanceXZ));


            ReflectionObject packetPlayOutEntityHeadRotation = NMSUtil.getNMSClass("PacketPlayOutEntityHeadRotation")
                    .getConstructor(NMSUtil.getNMSClass("Entity").getAClass(), byte.class)
                    .newInstance(npc, (byte) ((yaw % 360) * 256 / 360));

            ReflectionObject packetPlayOutEntityLook = NMSUtil.getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook")
                    .getConstructor(int.class, byte.class, byte.class, boolean.class)
                    .newInstance(id, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());


            PlayerNMS nms = PlayerNMS.cast(player);

            nms.sendPacket(packetPlayOutEntityHeadRotation.getObject());
            nms.sendPacket(packetPlayOutEntityLook.getObject());



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
            if (offHandNMS != null && XG7Plugins.getMinecraftVersion() > 8) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("OFFHAND") : 5, offHandNMS));
            if (helmetNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("HEAD") : 4, helmetNMS));
            if (chestplateNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("CHEST") : 3, chestplateNMS));
            if (leggingsNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("LEGS") : 2, leggingsNMS));
            if (bootsNMS != null) equipmentList.add(new Pair<>(XG7Plugins.getMinecraftVersion() > 8 ? enumItemSlotClass.getEnumField("FEET") : 1, bootsNMS));


            this.equipments = equipmentList;

            Bukkit.getOnlinePlayers().forEach(this::destroy);


    }
}
