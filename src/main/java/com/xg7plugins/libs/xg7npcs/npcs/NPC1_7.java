package com.xg7plugins.libs.xg7npcs.npcs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NPC1_7 extends NPC {
    public NPC1_7(Plugin plugin, String id, String name, Location location) {
        super(plugin, id, Collections.singletonList(name), location);
    }

    @Override
    public void spawn(Player player) {

        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {

            try {
                PlayerNMS playerNMS = PlayerNMS.cast(player);

                ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

                ReflectionObject interactManager = NMSUtil.getNMSClass("PlayerInteractManager")
                        .getConstructor(NMSUtil.getNMSClass(XG7Plugins.getMinecraftVersion() > 13 ? "WorldServer" : "World").getAClass())
                        .newInstance(nmsWorld.getObject());

                GameProfile npcSkin = (GameProfile) skin;

                if (playerSkin) {

                    GameProfile playerGameProfile = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player).getMethod("getProfile").invoke();

                    npcSkin = new GameProfile(UUID.randomUUID(), (String) name);

                    npcSkin.getProperties().putAll(playerGameProfile.getProperties());

                }
                String newName = Text.format((String) name, plugin).getWithPlaceholders(player);
                GameProfile npcSkinNameReplaced = new GameProfile(UUID.randomUUID(), newName.substring(0, Math.min(newName.length(), 16)));

                npcSkinNameReplaced.getProperties().putAll(npcSkin.getProperties());

                ReflectionObject npc = NMSUtil.getNMSClass("EntityPlayer")
                        .getConstructor(NMSUtil.getNMSClass("MinecraftServer").getAClass(), NMSUtil.getNMSClass("WorldServer").getAClass(), XG7Plugins.getMinecraftVersion() > 7 ? GameProfile.class : net.minecraft.util.com.mojang.authlib.GameProfile.class, NMSUtil.getNMSClass("PlayerInteractManager").getAClass())
                        .newInstance(playerNMS.getCraftPlayerHandle().getField("server"), nmsWorld.getObject(), npcSkinNameReplaced, interactManager.getObject());

                npc.getMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
                        .invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

                ReflectionObject packetPlayOutPlayerInfo = NMSUtil.getNMSClass("PacketPlayOutPlayerInfo")
                                .getMethod("addPlayer", NMSUtil.getNMSClass("EntityPlayer").getAClass())
                                .invokeToRObject(npc.getObject());

                ReflectionObject packetPlayOutNamedEntitySpawn = NMSUtil.getNMSClass("PacketPlayOutNamedEntitySpawn")
                        .getConstructor(NMSUtil.getNMSClass("EntityHuman").getAClass())
                        .newInstance(npc.getObject());

                float yaw = location.getYaw();
                float pitch = location.getPitch();

                ReflectionObject packetPlayOutEntityHeadRotation = NMSUtil.getNMSClass("PacketPlayOutEntityHeadRotation")
                        .getConstructor(NMSUtil.getNMSClass("Entity").getAClass(), byte.class)
                        .newInstance(npc.getObject(), (byte) ((yaw % 360) * 256 / 360));

                ReflectionObject packetPlayOutEntityLook = NMSUtil.getNMSClass("PacketPlayOutRelEntityMoveLook")
                        .getConstructor(int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class)
                        .newInstance(npc.getMethod("getId").invoke(), (byte) 0, (byte) 0, (byte) 0, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());

                playerNMS.sendPacket(packetPlayOutPlayerInfo.getObject());
                playerNMS.sendPacket(packetPlayOutNamedEntitySpawn.getObject());
                playerNMS.sendPacket(packetPlayOutEntityHeadRotation.getObject());
                playerNMS.sendPacket(packetPlayOutEntityLook.getObject());

                if (!equipments.isEmpty()) {

                    for (Pair<?, ?> pair : equipments) {
                        ReflectionObject packetPlayOutEntityEquipment = NMSUtil.getNMSClass("PacketPlayOutEntityEquipment")
                                .getConstructor(int.class, int.class, NMSUtil.getNMSClass("ItemStack").getAClass())
                                .newInstance(npc.getMethod("getId").invoke(), pair.getFirst(), pair.getSecond());
                        playerNMS.sendPacket(packetPlayOutEntityEquipment.getObject());
                    }

                }

                ReflectionObject packetPlayOutPlayerInfo2 = NMSUtil.getNMSClass("PacketPlayOutPlayerInfo")
                                .getMethod("removePlayer", NMSUtil.getNMSClass("EntityPlayer").getAClass())
                                .invokeToRObject(npc.getObject());

                Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2.getObject()), 20L);

                npcIDS.put(player.getUniqueId(), npc.getMethod("getId").invoke());
                if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npc.getMethod("getId").invoke(), npc.getObject());
            } catch (Exception e) {
                e.printStackTrace();
            }


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

        ReflectionObject packetPlayOutEntityLook = NMSUtil.getNMSClass("PacketPlayOutRelEntityMoveLook")
                .getConstructor(int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class)
                .newInstance(id, (byte) 0, (byte) 0, (byte) 0, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), player.isOnGround());


        PlayerNMS nms = PlayerNMS.cast(player);

        nms.sendPacket(packetPlayOutEntityHeadRotation.getObject());
        nms.sendPacket(packetPlayOutEntityLook.getObject());

    }

    @Override
    public void setEquipment(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {

        Object mainHandNMS = mainHand == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(mainHand);
        Object helmetNMS = helmet == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(helmet);
        Object chestplateNMS = chestplate == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(chestplate);
        Object leggingsNMS = leggings == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(leggings);
        Object bootsNMS = boots == null ? null : NMSUtil.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(boots);

        List<Pair<?, ?>> equipmentList = new ArrayList<>();

        if (mainHandNMS != null) equipmentList.add(new Pair<>(0, mainHandNMS));
        if (helmetNMS != null) equipmentList.add(new Pair<>(4, helmetNMS));
        if (chestplateNMS != null) equipmentList.add(new Pair<>(3, chestplateNMS));
        if (leggingsNMS != null) equipmentList.add(new Pair<>(2, leggingsNMS));
        if (bootsNMS != null) equipmentList.add(new Pair<>(1, bootsNMS));


        this.equipments = equipmentList;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    @Override
    protected void initSkin() {
        this.skin = new GameProfile(UUID.randomUUID(), "dummyNPC");
        ((GameProfile) skin).getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTczMDEzMjM2NjY3OCwKICAicHJvZmlsZUlkIiA6ICI3MGQzMzg2YzU5NzA0NmU1YWM4OTNhYmZlYTQ5N2IxMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJST1lMRUU1NDYwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyNjZlZTZiY2MyNjY1ZTBhODQ3N2Q0OTUzN2RkZjZiMjk4YjVjNGE1NDU2MWIyZjNjNDQ4MmI3N2IzNzA2MSIKICAgIH0KICB9Cn0=", "wOX7HhZ9VNtfNJi3GJFT3LnbXUCaFtpcyQDldoWvmrbA5RjrQ8H2jVcBXpMxnlk9U43KvNgFNxy/d3KklSNg9EfOBmo5H2GYICIx9iJOTCnOZC8GLhZWuia8jC7lqB6CfT7TdZWZAT2CXM2b8pteGWjoPg+OWUuXyg6Jg0k7uUrqzjMYjfh6y7hJXZIl38hMgISymrdQPGQVGTdBKeDmrQDveYn49ZYKdAbeb3pEHM5/QZIlvZVdvEHoLS4U5QRiw5V3/ERvd36RlKaydZVveqSMAoWvak/etVTiT3gLA5VbJN/qWYjz3rkmNboouYDC6eWy75b8TZSkPtk02JZ/ILDgpvYPyrAXwpZQNtWLXF99zun+aSZFPaSgW6/28yItmeJ0i+HpYbtOEGF6lJnEtI/jWNc0qb8/daE+HiahcKndpwi2zlErjlFfry08P3u5R7iX/KbGsgn96pVt+G9SXBRLX84ymWaqsg70xA+wgSov0xTc6AMHG15aHSrryw+RAikDbMU4ooNazDmeMWsitQNa8c120TPUQM/h+/ysNdksjnxDkyjOekzpyJmalGorfBe/KbRVqd2fK5VwIh4wJqWvPP2Gofh0C1sawQf2fu0KHHHg8XQhT+MivvrYzs0rccHnRiYcbDX3IPUGqoedaD3Q+Gkqo33XRqq+IJKlAFM="));
    }
    @Override
    public void setSkin(String username) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
        String uuid = jsonObject.get("id").getAsString();

        url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        JsonObject skinProprieties = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();

        JsonObject properties = skinProprieties.getAsJsonArray("properties").get(0).getAsJsonObject();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) name);
        gameProfile.getProperties().put("textures", new Property("textures", properties.get("value").getAsString(), properties.get("signature").getAsString()));


        this.skin = gameProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
    public void setSkin(String value, String signature){

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) name);
        gameProfile.getProperties().put("textures", new net.minecraft.util.com.mojang.authlib.properties.Property("textures", value, signature));


        this.skin = gameProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
    public void setSkin(Player player) {

        ReflectionObject playerGameProfile = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player).getMethod("getProfile").invokeToRObject();

        GameProfile npcProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(UUID.randomUUID(), (String) name);
        npcProfile.getProperties().putAll(playerGameProfile.getMethod("getProperties").invoke());

        this.skin = npcProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void setPlayerSkin(boolean playerSkin) {
        this.playerSkin = playerSkin;
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
}
