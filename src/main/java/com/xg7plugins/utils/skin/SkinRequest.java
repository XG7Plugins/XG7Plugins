package com.xg7plugins.utils.skin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.MojangAPIUtil;
import com.google.gson.JsonObject;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Secrets;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.http.HTTPResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SkinRequest {

    private static final ObjectCache<UUID, Skin> cachedSkins = new ObjectCache<>(
            XG7Plugins.getInstance(),
            15 * 60 * 1000L,
            true,
            "skins",
            true,
            UUID.class,
            Skin.class
    );

    private static final ObjectCache<String, UUID> usernameToUUIDCache = new ObjectCache<>(
            XG7Plugins.getInstance(),
            15 * 60 * 1000L,
            true,
            "username-to-uuid",
            true,
            String.class,
            UUID.class
    );

    /**
     * Requests the skin of a player by their UUID.
     * @param uuid The UUID of the player whose skin is being requested.
     * @return A CompletableFuture that will complete with the Skin object.
     */
    public static CompletableFuture<Skin> requestSkinByUUID(UUID uuid) {
        if (cachedSkins.containsKey(uuid).join()) return CompletableFuture.completedFuture(cachedSkins.get(uuid).join());

        return CompletableFuture.supplyAsync(() -> cacheAndGet(uuid, new Skin(MojangAPIUtil.requestPlayerTextureProperties(uuid))));
    }

    /**
     * Requests the skin of a player by their Player object.
     * @param player The Player whose skin is being requested.
     * @return A CompletableFuture that will complete with the Skin object.
     */
    public static CompletableFuture<Skin> requestSkinByPlayer(Player player) {
        if (cachedSkins.containsKey(player.getUniqueId()).join()) return cachedSkins.get(player.getUniqueId());

        if (!Bukkit.getOnlineMode()) {
            return requestSkinByPlayerName(player.getName());
        }

        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        List<TextureProperty> properties = user.getProfile().getTextureProperties();

        return CompletableFuture.completedFuture(cacheAndGet(player.getUniqueId(), new Skin(properties)));
    }

    /**
     * Requests the skin of a player by their username.
     * @param username The username of the player whose skin is being requested.
     * @return A CompletableFuture that will complete with the Skin object.
     */
    public static CompletableFuture<Skin> requestSkinByPlayerName(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                String uuidString = usernameToUUIDCache.containsKey(username).join() ?
                        usernameToUUIDCache.get(username).join().toString() :
                        HTTP.get("https://api.mojang.com/users/profiles/minecraft/" + username).getJson().get("id").getAsString();

                UUID uuid = uuidString.contains("-") ? UUID.fromString(uuidString)
                        : UUID.fromString(uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"));

                if (!usernameToUUIDCache.containsKey(username).join()) usernameToUUIDCache.put(username, uuid).join();

                return requestSkinByUUID(uuid).join();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Requests a skin from an image URL.
     * @param url The URL of the image to create the skin from.
     * @return A CompletableFuture that will complete with the Skin object.
     */
    public static CompletableFuture<Skin> requestSkinByImageURL(String url) {

        UUID urlUUID = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));

        if (cachedSkins.containsKey(urlUUID).join()) return cachedSkins.get(urlUUID);

        String requestBody = "{\n" +
                "  \"url\": \"" + url + "\",\n" +
                "  \"visibility\": 0\n" +
                "}";

        return CompletableFuture.supplyAsync(() -> {
            try {

                HTTPResponse response = HTTP.post(
                        "https://api.mineskin.org/generate/url",
                        requestBody,
                        Arrays.asList(
                                Pair.of("Authorization", "Bearer " + Secrets.MINESKIN_API_KEY),
                                Pair.of("Content-Type", "application/json")
                        )
                );



                JsonObject result = response.getJson();

                JsonObject textureProperties = result.get("data").getAsJsonObject().get("texture").getAsJsonObject();

                String value = textureProperties.get("value").getAsString();
                String signature = textureProperties.get("signature").getAsString();

                return cacheAndGet(urlUUID, new Skin(value, signature));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Creates a Skin object from an InputStream of an image.
     * @param stream The InputStream of the image.
     * @return The Skin object created from the image.
     * @throws IOException If there is an error reading the InputStream.
     */
    public static CompletableFuture<Skin> requestSkinByImage(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] imageBytes = buffer.toByteArray();

        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        return requestSkinByImageURL("data:image/png;base64," + imageBase64);
    }

    /**
     * Clears the cached skin for a specific UUID.
     * @param uuid The UUID of the player whose cached skin should be cleared.
     */
    public static void clear(UUID uuid) {
        cachedSkins.remove(uuid).join();
    }

    /**
     * Clears all cached skins.
     */
    public static void clearCache() {
        cachedSkins.clear();
    }

    private static Skin cacheAndGet(UUID uuid, Skin skin) {
        if (!cachedSkins.containsKey(uuid).join()) cachedSkins.put(uuid, skin);
        return skin;
    }




}
