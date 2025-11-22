package com.xg7plugins.utils.skin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.MojangAPIUtil;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.utils.http.HTTP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinRequest {

    private static final ObjectCache<UUID, Skin> cachedSkins = new ObjectCache<>(
            XG7Plugins.getInstance(),
            15,
            true,
            "skins",
            true,
            UUID.class,
            Skin.class
    );

    public static CompletableFuture<Skin> requestSkinByUUID(UUID uuid) {
        if (cachedSkins.containsKey(uuid).join()) return CompletableFuture.completedFuture(cachedSkins.get(uuid).join());

        return CompletableFuture.supplyAsync(() -> cacheAndGet(uuid, new Skin(MojangAPIUtil.requestPlayerTextureProperties(uuid))));
    }

    public static CompletableFuture<Skin> requestSkinByPlayer(Player player) {
        if (cachedSkins.containsKey(player.getUniqueId()).join()) return cachedSkins.get(player.getUniqueId());

        if (!Bukkit.getOnlineMode()) {
            return requestSkinByPlayerName(player.getName());
        }

        User user = PacketEvents.getAPI().getPlayerManager().getUser(player.getUniqueId());

        List<TextureProperty> properties = user.getProfile().getTextureProperties();

        return CompletableFuture.completedFuture(cacheAndGet(player.getUniqueId(), new Skin(properties)));
    }

    public static CompletableFuture<Skin> requestSkinByPlayerName(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                String uuidString = HTTP.get("https://api.mojang.com/users/profiles/minecraft/" + username).getJson().get("id").getAsString();

                UUID uuid = uuidString.contains("-") ? UUID.fromString(uuidString)
                        : UUID.fromString(uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"));

                return requestSkinByUUID(uuid).join();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Skin> requestSkinByImageURL(String url) {

        if (cachedSkins.containsKey(UUID.fromString(url)).join()) return cachedSkins.get(UUID.fromString(url));

        return CompletableFuture.supplyAsync(() -> {
            try {
                return cacheAndGet(UUID.fromString(url), getSkinByImage(HTTP.get(url).getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Skin getSkinByImage(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] imageBytes = buffer.toByteArray();

        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        String texturesJson = String.format(
                "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"dummy\",\"textures\":{\"SKIN\":{\"url\":\"data:image/png;base64,%s\"}}}",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().replace("-", ""),
                imageBase64
        );

        String value = Base64.getEncoder().encodeToString(texturesJson.getBytes());

        return new Skin(value, "");
    }

    public static void clear(UUID uuid) {
        cachedSkins.remove(uuid).join();
    }
    public static void clearCache() {
        cachedSkins.clear();
    }

    private static Skin cacheAndGet(UUID uuid, Skin skin) {
        cachedSkins.put(uuid, skin);
        return skin;
    }




}
