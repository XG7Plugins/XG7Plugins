package com.xg7plugins.libs.xg7npcs.npcs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.Location;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Getter
public abstract class NPC {

    protected Object name;
    protected Object skin;
    protected Location location;
    protected Map<UUID, Integer> npcIDS;
    protected String id;
    protected List<Pair<?,?>> equipments;
    protected Plugin plugin;
    protected boolean lookAtPlayer = false;
    protected boolean playerSkin = false;

    public NPC(Plugin plugin, String id, List<String> name, Location location) {
        this.name = XG7Plugins.getMinecraftVersion() < 8 ? name.get(0) : HologramBuilder.creator(plugin,id + ":name").setLines(name).setLocation(location.add(0,-0.2,0)).build();

        if (XG7Plugins.getMinecraftVersion() > 7) XG7Plugins.getInstance().getHologramsManager().initTask();

        initSkin();

        this.npcIDS = new HashMap<>();
        this.location = location;
        this.equipments = new ArrayList<>();

        this.id = id;

        this.plugin = plugin;

        XG7Plugins.getInstance().getNpcManager().addNPC(this);
    }

    public void remove() {
        XG7Plugins.getInstance().getNpcManager().removeNPC(this);
        if (name instanceof Hologram) ((Hologram)name).remove();
        Bukkit.getOnlinePlayers().forEach(this::destroy);

    }

    public void setLookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    protected void initSkin() {
        this.skin = new GameProfile(UUID.randomUUID(), "dummyNPC");
        ((GameProfile) skin).getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTczMDEzMjM2NjY3OCwKICAicHJvZmlsZUlkIiA6ICI3MGQzMzg2YzU5NzA0NmU1YWM4OTNhYmZlYTQ5N2IxMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJST1lMRUU1NDYwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyNjZlZTZiY2MyNjY1ZTBhODQ3N2Q0OTUzN2RkZjZiMjk4YjVjNGE1NDU2MWIyZjNjNDQ4MmI3N2IzNzA2MSIKICAgIH0KICB9Cn0=", "wOX7HhZ9VNtfNJi3GJFT3LnbXUCaFtpcyQDldoWvmrbA5RjrQ8H2jVcBXpMxnlk9U43KvNgFNxy/d3KklSNg9EfOBmo5H2GYICIx9iJOTCnOZC8GLhZWuia8jC7lqB6CfT7TdZWZAT2CXM2b8pteGWjoPg+OWUuXyg6Jg0k7uUrqzjMYjfh6y7hJXZIl38hMgISymrdQPGQVGTdBKeDmrQDveYn49ZYKdAbeb3pEHM5/QZIlvZVdvEHoLS4U5QRiw5V3/ERvd36RlKaydZVveqSMAoWvak/etVTiT3gLA5VbJN/qWYjz3rkmNboouYDC6eWy75b8TZSkPtk02JZ/ILDgpvYPyrAXwpZQNtWLXF99zun+aSZFPaSgW6/28yItmeJ0i+HpYbtOEGF6lJnEtI/jWNc0qb8/daE+HiahcKndpwi2zlErjlFfry08P3u5R7iX/KbGsgn96pVt+G9SXBRLX84ymWaqsg70xA+wgSov0xTc6AMHG15aHSrryw+RAikDbMU4ooNazDmeMWsitQNa8c120TPUQM/h+/ysNdksjnxDkyjOekzpyJmalGorfBe/KbRVqd2fK5VwIh4wJqWvPP2Gofh0C1sawQf2fu0KHHHg8XQhT+MivvrYzs0rccHnRiYcbDX3IPUGqoedaD3Q+Gkqo33XRqq+IJKlAFM="));
    }
    public abstract void spawn(Player player);
    public abstract void destroy(Player player);
    public abstract void lookAtPlayer(Player player, int id, Object entity);
    public abstract void setEquipment(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots);
    public void teleport(Location location) {
        this.location = location;
        if (name instanceof Hologram) ((Hologram)name).setLocation(location.add(0,-0.2,0));
        Bukkit.getOnlinePlayers().forEach(player -> {
            destroy(player);
            if (name instanceof Hologram) ((Hologram)name).destroy(player);
        });
    };
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

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "dummyNPC");
        gameProfile.getProperties().put("textures", new Property("textures", properties.get("value").getAsString(), properties.get("signature").getAsString()));


        this.skin = gameProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
    public void setSkin(String value, String signature){

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "dummyNPC");
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));


        this.skin = gameProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }
    public void setSkin(Player player) {

        ReflectionObject playerGameProfile = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player).getMethod("getProfile").invokeToRObject();

        GameProfile npcProfile = new GameProfile(UUID.randomUUID(), "dummyNPC");
        npcProfile.getProperties().putAll(playerGameProfile.getMethod("getProperties").invoke());



        this.skin = npcProfile;

        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void setPlayerSkin(boolean playerSkin) {
        this.playerSkin = playerSkin;
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }


}
