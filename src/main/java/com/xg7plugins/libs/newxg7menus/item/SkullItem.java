package com.xg7plugins.libs.newxg7menus.item;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkullItem extends Item {

    private static final Cache<String, ItemMeta> cachedSkulls = Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();

    private boolean renderSkullPlayer = false;

    public SkullItem() {
        super(XMaterial.PLAYER_HEAD.parseItem());
    }

    /**
     * This method sets the skull skin value
     * @param value The skin value of the skull
     * @return This InventoryItem
     */
    public SkullItem setValue(String value) {
        if (XG7Plugins.getMinecraftVersion() < 8) {
            return this;
        }
        if (cachedSkulls.asMap().containsKey(value)) {
            this.itemStack.setItemMeta(cachedSkulls.getIfPresent(value));
            return this;
        }
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null");
        gameProfile.getProperties().put("textures", new Property("textures", value));

        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        cachedSkulls.put(value, skullMeta);
        super.meta(skullMeta);
        return this;
    }
    /**
     * This method sets the skull owner
     * @param owner The skin owner of the skull
     * @return This InventoryItem
     */
    public SkullItem setOwner(String owner) {
        if (XG7Plugins.getMinecraftVersion() < 8) {
            return this;
        }
        if (Bukkit.getOnlineMode() && Bukkit.getPlayer(owner) != null) {
            setPlayerSkinValue(Bukkit.getPlayer(owner).getUniqueId());
            return this;
        }
        if (cachedSkulls.asMap().containsKey(owner)) {
            meta(cachedSkulls.getIfPresent(owner));
            return this;
        }
        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
        meta.setOwner(owner);
        cachedSkulls.put(owner, meta);
        super.meta(meta);
        return this;
    }

    /**
     * This method sets the skull skin value with the player skin value
     * @param player The player that will be used to get the skin value
     * @return This InventoryItem
     */
    public SkullItem setPlayerSkinValue(UUID player) {
        if (XG7Plugins.getMinecraftVersion() < 8) {
            return this;
        }
        if (cachedSkulls.asMap().containsKey(player.toString())) {
            this.itemStack.setItemMeta(cachedSkulls.getIfPresent(player.toString()));
            return this;
        }
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                XG7Plugins.getInstance().getLog().severe("Erro ao colocar valor de player na skin da cabeça!");
                return this;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            JsonObject profileData = new JsonParser().parse(sb.toString()).getAsJsonObject();
            JsonObject properties = profileData.getAsJsonArray("properties").get(0).getAsJsonObject();


            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", properties.get("value").getAsString()));

            SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();


            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            cachedSkulls.put(player.toString(),skullMeta);
            super.meta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public <T extends HumanEntity> ItemStack getItemFor(T player, Plugin plugin) {
        ItemStack prepared = super.getItemFor(player, plugin);

        if (renderSkullPlayer) setOwner(((Player) player).getDisplayName());

        return prepared;

    }

}
