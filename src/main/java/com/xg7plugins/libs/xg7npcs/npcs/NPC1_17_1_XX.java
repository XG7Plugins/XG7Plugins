package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.EntityDataWatcher1_17_1_XX;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NPC1_17_1_XX extends NPC {
    public NPC1_17_1_XX(Plugin plugin, List<String> name, Location location) {
        super(plugin, name, location);
    }

    @Override
    public void spawn(Player player) {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
            try {
                System.out.println("Casting Player\n");
                PlayerNMS playerNMS = PlayerNMS.cast(player);
                System.out.println("Creating npc\n");

                EntityPlayer npc = new EntityPlayer(
                        playerNMS.getCraftPlayerHandle().getMethod("getMinecraftServer").invoke(),
                        playerNMS.getCraftPlayerHandle().getMethod("getWorld").invoke(),
                        skin);

                System.out.println("Setting npc position\n");
                npc.setPosition(location.getX(), location.getY(), location.getZ());
                npc.setYawPitch(-location.getYaw(), -location.getPitch());
                npc.setPose(EntityPose.c);

                System.out.println("Creating datawatcher\n");
                EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();
                dataWatcher.watch(3, false);
                dataWatcher.watch(5, true);

                System.out.println("Packet player info\n");
                PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc);

                PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(npc);

                System.out.println("Packet metadata\n");
                PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher.getWatcher(), true);

                playerNMS.sendPacket(packetPlayOutPlayerInfo);
                playerNMS.sendPacket(entitySpawn);
                playerNMS.sendPacket(metadata);
                Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> {
                    playerNMS.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
                },20L);

                entityIDS.add(npc.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


    }

    @Override
    public void destroy(Player player) {

    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void setSkin(String value) {



    }
}
