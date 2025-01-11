package com.xg7plugins.libs.xg7npcs.npcs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.nms.Packet;
import com.xg7plugins.utils.reflection.nms.PacketClass;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
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
import java.util.*;

public class NPC1_7 extends NPC {

    private static ReflectionClass craftWorldClass;
    private static ReflectionClass playerInteractManager;
    private static ReflectionClass worldClass;
    private static ReflectionClass worldServer;
    private static ReflectionClass entityPlayerClass;
    private static ReflectionClass entityClass;
    private static ReflectionClass entityHumanClass;
    private static ReflectionClass minecraftClass;
    private static ReflectionClass craftItemStackClass;

    private static PacketClass packetPlayOutPlayerInfoClass;
    private static PacketClass packetPlayOutNamedEntitySpawnClass;
    private static PacketClass packetPlayOutEntityHeadRotationClass;
    private static PacketClass packetPlayOutEntityLookClass;
    private static PacketClass packetPlayOutEntityEquipmentClass;
    private static PacketClass packetPlayOutEntityDestroyClass;

    static {
        try {
            craftWorldClass = NMSUtil.getCraftBukkitClass("CraftWorld");
            playerInteractManager = NMSUtil.getNMSClass("PlayerInteractManager");
            worldClass = NMSUtil.getNMSClass("World");
            worldServer = NMSUtil.getNMSClass("WorldServer");
            entityPlayerClass = NMSUtil.getNMSClass("EntityPlayer");
            minecraftClass = NMSUtil.getNMSClass("MinecraftServer");
            craftItemStackClass = NMSUtil.getCraftBukkitClass("inventory.CraftItemStack");
            entityHumanClass = NMSUtil.getNMSClass("EntityHuman");
            entityClass = NMSUtil.getNMSClass("Entity");

            packetPlayOutPlayerInfoClass = new PacketClass("PacketPlayOutPlayerInfo");
            packetPlayOutNamedEntitySpawnClass = new PacketClass("PacketPlayOutNamedEntitySpawn");
            packetPlayOutEntityHeadRotationClass = new PacketClass("PacketPlayOutEntityHeadRotation");
            packetPlayOutEntityLookClass = new PacketClass("PacketPlayOutRelEntityMoveLook");
            packetPlayOutEntityEquipmentClass = new PacketClass("PacketPlayOutEntityEquipment");
            packetPlayOutEntityDestroyClass = new PacketClass("PacketPlayOutEntityDestroy");
        } catch (Exception ignored) {

        }

    }

    public NPC1_7(Plugin plugin, String id, String name, Location location) {
        super(plugin, id, Collections.singletonList(name), location);
    }

    @Override
    public void spawn(Player player) {

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        ReflectionObject world = craftWorldClass.castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();

        ReflectionObject interactManager = playerInteractManager.getConstructor(worldClass.getAClass()).newInstance(world.getObject());


        GameProfile npcSkin = (GameProfile) skin;

        if (playerSkin) {

            GameProfile playerGameProfile = playerNMS.getCraftPlayerHandle().getMethod("getProfile").invoke();

            npcSkin = new GameProfile(UUID.randomUUID(), (String) name);

            npcSkin.getProperties().putAll(playerGameProfile.getProperties());

        }
        String newName = Text.format((String) name).getTextFor(player);
        GameProfile npcSkinNameReplaced = new GameProfile(UUID.randomUUID(), newName.substring(0, Math.min(newName.length(), 16)));

        npcSkinNameReplaced.getProperties().putAll(npcSkin.getProperties());

        ReflectionObject npc = entityPlayerClass
                .getConstructor(minecraftClass.getAClass(), worldServer.getAClass(), GameProfile.class, playerInteractManager.getAClass())
                .newInstance(playerNMS.getCraftPlayerHandle().getField("server"), world.getObject(), npcSkinNameReplaced, interactManager.getObject());

        npc.getMethod("setPositionRotation",
               double.class,
                double.class,
                double.class,
                float.class,
                float.class
                ).invoke(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        Packet packetPlayOutPlayerInfo  = new Packet(packetPlayOutPlayerInfoClass.getReflectionClass()
                .getMethod("addPlayer", entityPlayerClass.getAClass())
                .invoke(npc.getObject())
        );

        Packet packetPlayOutNamedEntitySpawn = new Packet(packetPlayOutNamedEntitySpawnClass, new Class[]{entityHumanClass.getAClass()}, npc.getObject());

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Packet packetPlayOutEntityHeadRotation = new Packet(packetPlayOutEntityHeadRotationClass, new Class<?>[]{entityClass.getAClass(),byte.class}, npc.getObject(), (byte) ((yaw % 360) * 256 / 360));

        Packet packetPlayOutEntityLook;

        try {
            packetPlayOutEntityLook = new Packet(packetPlayOutEntityLookClass, (int) npc.getMethod("getId").invoke(), (byte) 0, (byte) 0, (byte) 0, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360),false);
        } catch (Exception ignored) {
            packetPlayOutEntityLook = new Packet(packetPlayOutEntityLookClass, (int) npc.getMethod("getId").invoke(), (byte) 0, (byte) 0, (byte) 0, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360));
        };

        Packet packetPlayOutPlayerInfo2  = new Packet(packetPlayOutPlayerInfoClass.getReflectionClass()
                .getMethod("removePlayer", entityPlayerClass.getAClass())
                .invoke(npc.getObject())
        );


        Packet finalPacketPlayOutEntityLook = packetPlayOutEntityLook;
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> {

            playerNMS.sendPacket(packetPlayOutPlayerInfo);
            playerNMS.sendPacket(packetPlayOutNamedEntitySpawn);
            playerNMS.sendPacket(packetPlayOutEntityHeadRotation);
            playerNMS.sendPacket(finalPacketPlayOutEntityLook);

            if (!equipments.isEmpty()) {

                for (Pair<?, ?> pair : equipments) {

                    Packet packetPlayOutEntityEquipment = new Packet(packetPlayOutEntityEquipmentClass, (int) npc.getMethod("getId").invoke(), pair.getFirst(), pair.getSecond());

                    playerNMS.sendPacket(packetPlayOutEntityEquipment);
                }

            }

            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> playerNMS.sendPacket(packetPlayOutPlayerInfo2), 20L);
        });

        npcIDS.put(player.getUniqueId(), npc.getMethod("getId").invoke());
        if (lookAtPlayer) XG7Plugins.getInstance().getNpcManager().registerLookingNPC(npc.getMethod("getId").invoke(), npc);


    }


    @Override
    public void destroy(Player player) {
        if (!npcIDS.containsKey(player.getUniqueId())) return;

        Packet packetPlayOutEntityDestroy = new Packet(packetPlayOutEntityDestroyClass, new Class[]{int[].class}, new int[]{npcIDS.get(player.getUniqueId())});

        PlayerNMS.cast(player).sendPacket(packetPlayOutEntityDestroy);

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


        Packet packetPlayOutEntityHeadRotation = new Packet(packetPlayOutEntityHeadRotationClass, npc, (byte) ((yaw % 360) * 256 / 360));

        Packet packetPlayOutEntityLook = new Packet(packetPlayOutEntityLookClass, new Class<?>[]{int.class,byte.class,byte.class,byte.class,byte.class,byte.class}, ReflectionObject.of(npc).getMethod("getId").invoke(), (byte) 0, (byte) 0, (byte) 0, (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360));

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        playerNMS.sendPacket(packetPlayOutEntityHeadRotation);
        playerNMS.sendPacket(packetPlayOutEntityLook);

    }

    @Override
    public void setEquipment(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {

        List<Pair<?, ?>> equipmentList = new ArrayList<>();

        if (mainHand != null) equipmentList.add(new Pair<>(0, craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(mainHand)));
        if (helmet != null) equipmentList.add(new Pair<>(4, craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(helmet)));
        if (chestplate != null) equipmentList.add(new Pair<>(3, craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(chestplate)));
        if (leggings != null) equipmentList.add(new Pair<>(2, craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(leggings)));
        if (boots != null) equipmentList.add(new Pair<>(1, craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(boots)));

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
