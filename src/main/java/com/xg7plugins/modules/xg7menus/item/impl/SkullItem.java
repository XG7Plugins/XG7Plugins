package com.xg7plugins.modules.xg7menus.item.impl;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.http.HTTPResponse;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SkullItem extends Item {

    private static final ObjectCache<UUID, String> cachedSkulls = new ObjectCache<>(
            XG7Plugins.getInstance(), 15, true, "skulls", true, UUID.class, String.class
    );

    private boolean renderSkullPlayer = false;

    public SkullItem() {
        super(XMaterial.PLAYER_HEAD.parseItem());
    }
    public SkullItem(ItemStack itemStack) {
        super(itemStack);
    }

    public SkullItem renderPlayerSkull(boolean render) {
        this.renderSkullPlayer = render;
        return this;
    }

    public static SkullItem from(ItemStack itemStack) {
        return new SkullItem(itemStack);
    }
    public static SkullItem newSkull() {
        return new SkullItem();
    }

    /**
     * This method sets the skull skin value
     * @param value The skin value of the skull
     * @return This InventoryItem
     */
    public SkullItem setValue(String value) {
        if (MinecraftVersion.isOlderThan(8)) {
            return this;
        }

        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

        setValueOf(skullMeta, value);

        super.meta(skullMeta);
        return this;
    }
    /**
     * This method sets the skull owner
     * @param owner The skin owner of the skull
     * @return This InventoryItem
     */
    public SkullItem setOwner(String owner) {
        if (MinecraftVersion.isOlderThan(8)) {
            return this;
        }

        Player player = Bukkit.getPlayer(owner);

        if (player == null) {
            SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();

            meta.setOwner(owner);
            super.meta(meta);
            return this;
        }

        setSkinByPlayer(Bukkit.getPlayer(owner));
        return this;
    }

    public SkullItem setSkinByPlayer(Player player) {
        if (MinecraftVersion.isOlderThan(8)) {
            return this;
        }

        if (MinecraftVersion.isNewerThan(16) && XG7PluginsAPI.getServerSoftware().isPaper()) {
            ReflectionObject playerProfile = ReflectionObject.of(player)
                    .getMethod("getPlayerProfile")
                    .invokeToRObject();

            Set<Object> properties = playerProfile.getMethod("getProperties").invoke();

            if (properties != null && !properties.isEmpty()) {
                Object property = properties.iterator().next();

                String textureValue = ReflectionObject.of(property)
                        .getMethod("getValue")
                        .invoke();

                if (textureValue != null) {
                    setValueOf((SkullMeta) this.itemStack.getItemMeta(), textureValue);
                }
            }
            return this;
        }

        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();

        GameProfile profile = ReflectionObject.of(player)
                .getMethod("getProfile")
                .invoke();

        if (profile == null) return this;

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * This method sets the skull skin value with the player skin value
     * @return This InventoryItem
     */
    public SkullItem setSkinByUUID(UUID uuid) {
        if (MinecraftVersion.isOlderThan(8)) {
            return this;
        }
        if (cachedSkulls.containsKey(uuid).join()) {
            setValue(cachedSkulls.get(uuid).join());
            return this;
        }

        try {
            HTTPResponse response = HTTP.get(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid,
                    Collections.singletonList(Pair.of("Accept", "application/json"))
            );

            if (response.getStatusCode() != 200) {
                XG7Plugins.getInstance().getDebug().severe("Error to put skin value on player head!");
                return this;
            }

            JsonObject profileData = response.getJson();
            JsonObject properties = profileData.getAsJsonArray("properties").get(0).getAsJsonObject();

            SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

            setValueOf(skullMeta, properties.get("value").getAsString());

            cachedSkulls.put(uuid, properties.get("value").getAsString());

            super.meta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }


    @Override
    public ItemStack getItemFor(CommandSender player, Plugin plugin) {
        ItemStack prepared = super.getItemFor(player, plugin);

        if (!(player instanceof Player)) return prepared;

        if (renderSkullPlayer) {
            return SkullItem.from(prepared).setOwner(((Player) player).getDisplayName()).getItemStack();
        }

        return prepared;

    }

    private void setValueOf(SkullMeta meta, String value) {
        if (MinecraftVersion.isNewerThan(16) && XG7PluginsAPI.getServerSoftware().isPaper()) {

            ReflectionObject paperProfile = ReflectionClass.of(Bukkit.class).getMethod("createProfile", UUID.class).invokeToRObject(UUID.randomUUID());

            paperProfile.getMethod(
                            "setProperty",
                            ReflectionClass.of("com.destroystokyo.paper.profile.ProfileProperty").getAClass()
                    )
                    .invoke(
                            ReflectionClass.of("com.destroystokyo.paper.profile.ProfileProperty")
                                    .getConstructor(String.class, String.class)
                                    .newInstance("textures", value).getObject()
                    );

            ReflectionObject.of(meta)
                    .getMethod("setPlayerProfile", ReflectionClass.of("com.destroystokyo.paper.profile.PlayerProfile").getAClass())
                    .invoke(paperProfile.getObject());

            super.meta(meta);
            return;
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null");
        gameProfile.getProperties().put("textures", new Property("textures", value));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
