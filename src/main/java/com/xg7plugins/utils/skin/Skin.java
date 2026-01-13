package com.xg7plugins.utils.skin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Minecraft skin with texture properties.
 * Provides methods to convert to user profiles and handle server version differences.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Skin {

    private final String value;
    private final String signature;

    public Skin(List<TextureProperty> properties) {
        if (properties.isEmpty()) {
            this.value = null;
            this.signature = null;
            return;
        }

        this.value = properties.get(0).getValue();
        this.signature = properties.get(0).getSignature();
    }

    /**
     * Converts the skin to a UserProfile with the given UUID and name.
     * @param uuid The UUID of the user
     * @param name The name of the user
     * @return The UserProfile with the skin applied
     */
    public UserProfile toProfile(UUID uuid, String name) {
        UserProfile profile = new UserProfile(uuid, name);

        if (value == null && signature == null) return profile;

        profile.setTextureProperties(Collections.singletonList(new TextureProperty("textures", value, signature)));

        return profile;
    }

    /**
     * Converts the skin to a UserProfile with a random UUID and the given name.
     * @param name The name of the user
     * @return The UserProfile with the skin applied
     */
    public UserProfile toBaseProfile(String name) {
        return toProfile(UUID.randomUUID(), name);
    }

    /**
     * Converts the skin to a UserProfile with a random UUID and a dummy name.
     * @return The UserProfile with the skin applied
     */
    public UserProfile toBaseProfile() {
        return toBaseProfile("dummy");
    }

    /**
     * Converts the skin to an ItemProfile with the given UUID and name.
     * @param id The UUID to assign to the item profile
     * @param name The name for the item profile
     * @return The ItemProfile with texture properties applied
     */
    public ItemProfile toItemProfile(UUID id, String name) {

        List<ItemProfile.Property> properties = new ArrayList<>();

        if (value != null && signature != null) {
            properties.add(new ItemProfile.Property("textures", value, signature));
        }

        return new ItemProfile(name, id, properties);
    }

    /**
     * Converts the skin to an ItemProfile with a random UUID and the given name.
     * @param name The name for the item profile
     * @return The ItemProfile with texture properties applied
     */
    public ItemProfile toItemProfile(String name) {
        return toItemProfile(UUID.randomUUID(), name);
    }

    /**
     * Converts the skin to an ItemProfile with a random UUID and a dummy name.
     * @return The ItemProfile with texture properties applied
     */
    public ItemProfile toItemProfile() {
        return toItemProfile("dummy");
    }

    /**
     * Gets the appropriate profile object based on server software and version.
     * @return The profile object
     */
    public ReflectionObject getProfileBySoftwareAndVersion() {
        return getProfileBySoftwareAndVersion(this);
    }


    /**
     * Creates a Skin from a PacketEvents UserProfile.
     * @param profile the UserProfile to extract texture properties from
     * @return a Skin containing the first texture property's value and signature,
     * or an empty Skin (null value and signature) if no properties are present
     */
    public static Skin ofProfile(UserProfile profile) {
        List<TextureProperty> properties = profile.getTextureProperties();
        if (properties.isEmpty()) {
            return new Skin(null, null);
        }
        TextureProperty textureProperty = properties.get(0);
        return new Skin(textureProperty.getValue(), textureProperty.getSignature());
    }

    /**
     * Creates a Skin from an ItemProfile.
     * @param profile the ItemProfile to extract properties from
     * @return a Skin containing the first item property's value and signature,
     * or an empty Skin if no properties are present
     */
    public static Skin ofItemProfile(ItemProfile profile) {
        List<ItemProfile.Property> properties = profile.getProperties();
        if (properties.isEmpty()) {
            return new Skin(null, null);
        }
        ItemProfile.Property textureProperty = properties.get(0);
        return new Skin(textureProperty.getValue(), textureProperty.getSignature());
    }

    /**
     * Creates a Skin from a PacketEvents User by extracting its UserProfile.
     * @param user the PacketEvents User
     * @return a Skin constructed from the user's profile
     */
    public static Skin ofUser(User user) {
        return ofProfile(user.getProfile());
    }

    /**
     * Retrieves the PacketEvents User for the given Bukkit Player and returns its Skin.
     * If the user cannot be obtained, an empty Skin is returned.
     * @param player the Bukkit Player
     * @return a Skin for the player or an empty Skin if the user is unavailable
     */
    public static Skin ofPlayer(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) {
            return new Skin(null, null);
        }
        return ofUser(user);
    }



    /**
     * Gets the appropriate profile object based on server software and version.
     * @param skin The skin to convert
     * @return The profile object
     */
    public static ReflectionObject getProfileBySoftwareAndVersion(Skin skin) {

        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) return ReflectionObject.of(skin.toBaseProfile());

        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_8_8) && XG7Plugins.getAPI().getServerSoftware().isPaper()) {
            ReflectionObject paperProfile = ReflectionClass.of(Bukkit.class).getMethod("createProfile", UUID.class).invokeToRObject(UUID.randomUUID());

            paperProfile.getMethod(
                            "setProperty",
                            ReflectionClass.of("com.destroystokyo.paper.profile.ProfileProperty").getAClass()
                    )
                    .invoke(
                            ReflectionClass.of("com.destroystokyo.paper.profile.ProfileProperty")
                                    .getConstructor(String.class, String.class)
                                    .newInstance("textures", skin.getValue()).getObject()
                    );

            return paperProfile;
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null");
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue()));

        return ReflectionObject.of(gameProfile);
    }
}
