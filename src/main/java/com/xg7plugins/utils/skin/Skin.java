package com.xg7plugins.utils.skin;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
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
     * Gets the appropriate profile object based on server software and version.
     * @return The profile object
     */
    public ReflectionObject getProfileBySoftwareAndVersion() {
        return getProfileBySoftwareAndVersion(this);
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
