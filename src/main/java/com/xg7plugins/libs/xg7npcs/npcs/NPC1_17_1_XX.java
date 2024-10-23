package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.reflection.EntityDataWatcher1_17_1_XX;
import com.xg7plugins.utils.reflection.PlayerNMS;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NPC1_17_1_XX extends NPC {
    public NPC1_17_1_XX(Plugin plugin, List<String> name, Location location) {
        super(plugin, name, new GameProfile(UUID.randomUUID(), "dummy"), location);
    }

    @Override
    public void spawn(Player player) {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {
            try {
                System.out.println("Casting Player\n");
                PlayerNMS playerNMS = PlayerNMS.cast(player);
                System.out.println("Creating npc\n");

                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "dummy");
                gameProfile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NjNzg3ZDJlMWJkOWI4ZjI3NWYyMGY4ZjY5ZWMxZDFmMDhjZmZiNjcwZmUzYWQ4ZDk0YjlkMDEwZGViMGMyYSJ9fX0="));

                EntityPlayer npc = new EntityPlayer(
                        playerNMS.getCraftPlayerHandle().getMethod("getMinecraftServer").invoke(),
                        playerNMS.getCraftPlayerHandle().getMethod("getWorld").invoke(),
                        gameProfile);
                System.out.println("Setting npc position\n");
                npc.setPosition(location.getX(), location.getY(), location.getZ());
                npc.setPose(EntityPose.c);


                System.out.println("Creating datawatcher\n");
                EntityDataWatcher1_17_1_XX dataWatcher = new EntityDataWatcher1_17_1_XX();
                dataWatcher.watch(2, "I'm a npc");
                dataWatcher.watch(3, false);
                dataWatcher.watch(5, true);

                System.out.println("Packet player info\n");
                PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc);
                playerNMS.sendPacket(packetPlayOutPlayerInfo);

                PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
                playerNMS.sendPacket(entitySpawn);

                System.out.println("Packet metadata\n");
                PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher.getWatcher(), true);
                playerNMS.sendPacket(metadata);
                Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> {
                    playerNMS.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
                },20L);
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
    public void setSkin(GameProfile skin) {

    }
}
