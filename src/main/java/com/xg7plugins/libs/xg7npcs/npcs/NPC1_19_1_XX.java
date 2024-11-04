package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NPC1_19_1_XX extends NPC {

    private static final ReflectionClass minecraftServerClass = NMSUtil.getNewerNMSClass("server.MinecraftServer");
    private static final ReflectionClass worldServerClass = NMSUtil.getNewerNMSClass("server.level.WorldServer");
    private static final ReflectionClass entityPoseClass = NMSUtil.getNewerNMSClass("world.entity.EntityPose");
    private static final ReflectionClass packetPlayOutEntityDestroyClass = NMSUtil.getNewerNMSClass("network.protocol.game.PacketPlayOutEntityDestroy");


    public NPC1_19_1_XX(Plugin plugin, String id, List<String> name, Location location) {
        super(plugin, id, name, location);
    }

    @Override
    public void spawn(Player player) {

        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {

            try {
                PlayerNMS playerNMS = PlayerNMS.cast(player);
                ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

                EntityPlayer npc;

                if (XG7Plugins.getMinecraftVersion() < 21) {
                    npc = new EntityPlayer(
                            playerNMS.getCraftPlayerHandle().getField("d"),
                            (WorldServer) nmsWorld.getObject(),
                            skin
                    );
                } else {
                    ReflectionObject clientInformation = NMSUtil.getNewerNMSClass("server.level.ClientInformation")
                            .getConstructor(
                                    String.class,
                                    int.class,
                                    EnumChatVisibility.class,
                                    boolean.class,
                                    int.class,
                                    EnumMainHand.class,
                                    boolean.class,
                                    boolean.class
                            ).newInstance(
                                    "en_us",
                                    2,
                                    EnumChatVisibility.a,
                                    true,
                                    70,
                                    EnumMainHand.b,
                                    true,
                                    true
                            );

                    npc = (EntityPlayer) ReflectionClass.of(EntityPlayer.class).getConstructor(
                            minecraftServerClass.getAClass(),
                            worldServerClass.getAClass(),
                            GameProfile.class,
                            NMSUtil.getNewerNMSClass("server.level.ClientInformation").getAClass()
                    ).newInstance(
                            playerNMS.getCraftPlayerHandle().getField("d"),
                            nmsWorld.getObject(),
                            skin,
                            clientInformation.getObject()
                    ).getObject();
                }



                npc.c(EntityPose.a);
                npc.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

                int npcId = ReflectionObject.of(npc).getFieldFromSuperClass(Entity.class, "q");

                ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, npc);
                PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(npc);

                EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();
                dataWatcher.watch(17, (byte) 0x07F);

                PacketPlayOutEntityHeadRotation rotation = new PacketPlayOutEntityHeadRotation(npc, (byte) (location.getYaw() * 256 / 360));
                PacketPlayOutEntity.PacketPlayOutEntityLook bodyRotation = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                        npcId,
                        (byte) (location.getYaw() * 256 / 360),
                        (byte) (location.getPitch() * 256 / 360),
                        true
                );
                ScoreboardTeam team = new ScoreboardTeam(new Scoreboard(), "dummyNPC");

                team.a(ScoreboardTeamBase.EnumNameTagVisibility.b);
                team.g().add("dummyNPC");

                PacketPlayOutScoreboardTeam teamPacket = PacketPlayOutScoreboardTeam.a(team, true);

                PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(npcId, (List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>) equipments);

                //PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(npcId, ((DataWatcher)dataWatcher.getWatcher().getObject()).b());

                playerNMS.sendPacket(packet);
                playerNMS.sendPacket(spawnPacket);
                playerNMS.sendPacket(rotation);
                playerNMS.sendPacket(bodyRotation);
                playerNMS.sendPacket(teamPacket);
                if (!equipments.isEmpty()) {
                    playerNMS.sendPacket(equipment);
                }
                //playerNMS.sendPacket(packetPlayOutEntityMetadata);

                ClientboundPlayerInfoUpdatePacket removePacket = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.b, npc);

                Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(removePacket));

                npcIDS.put(player.getUniqueId(), npcId);
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

        if (mainHandNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.a, mainHandNMS));
        if (offHandNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.b, offHandNMS));
        if (helmetNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.f, helmetNMS));
        if (chestplateNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.e, chestplateNMS));
        if (leggingsNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.d, leggingsNMS));
        if (bootsNMS != null) equipmentList.add(new Pair<>(EnumItemSlot.c, bootsNMS));

        this.equipments = equipmentList;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
}
