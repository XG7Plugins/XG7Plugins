package com.xg7plugins.utils.item.impl;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.skin.Skin;
import com.xg7plugins.utils.skin.SkinRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullItem extends Item {

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
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) {
            return this;
        }

        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

        if (MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_18_2) && !XG7Plugins.getAPI().getServerSoftware().isPaper()) {
            try {
                setByURL(new URL(value));
                return this;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        Skin skin = new Skin(value, "");

        ReflectionObject profile = skin.getProfileBySoftwareAndVersion();

        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_16_5) && XG7Plugins.getAPI().getServerSoftware().isPaper()) {

            ReflectionObject.of(skullMeta)
                    .getMethod("setPlayerProfile", ReflectionClass.of("com.destroystokyo.paper.profile.PlayerProfile").getAClass())
                    .invoke(profile.getObject());

            super.meta(skullMeta);
            return this;
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null");
        gameProfile.getProperties().put("textures", new Property("textures", value));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        super.meta(skullMeta);
        return this;
    }
    /**
     * This method sets the skull owner
     * @param owner The skin owner of the skull
     * @return This InventoryItem
     */
    public SkullItem setOwner(String owner) {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) {
            return this;
        }

        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();

        meta.setOwner(owner);
        super.meta(meta);

        Player player = Bukkit.getPlayer(owner);

        if (player == null) {

            SkinRequest.requestSkinByPlayerName(owner).thenAccept(result -> {
                if (result == null) return;

                setValue(result.getValue());
            });
            return this;
        }

        setSkinByPlayer(Bukkit.getPlayer(owner));
        return this;
    }

    public SkullItem setSkinByPlayer(Player player) {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) {
            return this;
        }

        Skin skin = SkinRequest.requestSkinByPlayer(player).join();

        if (skin == null) return this;

        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();

        if (MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_18_2)) {
            ReflectionObject playerProfile = ReflectionObject.of(player)
                    .getMethod("getPlayerProfile")
                    .invokeToRObject();

            if (playerProfile.getObject() == null) return this;

            ReflectionObject.of(meta).getMethod("setOwnerProfile", ReflectionClass.of("org.bukkit.profile.PlayerProfile").getAClass())
                    .invoke(playerProfile.getObject());
            return this;
        }

        setValue(skin.getValue());

        return this;
    }

    /**
     * This method sets the skull skin value with the player skin value
     * @return This InventoryItem
     */
    public SkullItem setSkinByUUID(UUID uuid) {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) {
            return this;
        }
        return setValue(SkinRequest.requestSkinByUUID(uuid).join().getValue());
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
    public void setByURL(URL url) {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_18_2)) return;
        if (XG7Plugins.getAPI().getServerSoftware().isPaper()) return;

        ReflectionObject playerProfile = ReflectionClass.of(Bukkit.class)
                .getMethod("createProfile", UUID.class)
                .invokeToRObject(UUID.randomUUID());

        ReflectionObject textures = ReflectionObject.of(playerProfile).getMethod("getTextures").invokeToRObject();

        textures.getMethod("setSkin", URL.class).invoke(url);

        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();

        ReflectionObject.of(meta).getMethod("setOwnerProfile", ReflectionClass.of("org.bukkit.profile.PlayerProfile").getAClass())
                .invoke(playerProfile.getObject());
    }

}
