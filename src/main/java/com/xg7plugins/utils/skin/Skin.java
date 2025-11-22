package com.xg7plugins.utils.skin;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public UserProfile toProfile(UUID uuid, String name) {
        UserProfile profile = new UserProfile(uuid, name);

        if (value == null && signature == null) return profile;

        profile.setTextureProperties(Collections.singletonList(new TextureProperty("textures", value, signature)));

        return profile;
    }

    public UserProfile toBaseProfile(String name) {
        return toProfile(UUID.randomUUID(), name);
    }

    public UserProfile toBaseProfile() {
        return toBaseProfile("dummy");
    }

    public ReflectionObject getProfileBySoftwareAndVersion() {
        return getProfileBySoftwareAndVersion(this);
    }

    public static ReflectionObject getProfileBySoftwareAndVersion(Skin skin) {

        if (MinecraftVersion.isOlderThan(8)) return ReflectionObject.of(skin.toBaseProfile());

        if (MinecraftVersion.isNewerThan(16) && XG7Plugins.getAPI().getServerSoftware().isPaper()) {
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
