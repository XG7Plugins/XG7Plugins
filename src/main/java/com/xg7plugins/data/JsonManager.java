package com.xg7plugins.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.config.file.ConfigFile;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.FileUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Manages JSON data operations including saving, loading, and caching.
 * Provides functionality for serializing/deserializing objects to/from the JSON format
 * with support for type adapters and caching mechanism.
 */
public class JsonManager {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final ObjectCache<String, Object> cache;
    public JsonManager(XG7Plugins plugin) {
        cache = new ObjectCache<>(
                plugin,
                ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("json-cache-expires", 10 * 60 * 1000L),
                false,
                "json-cache",
                false,
                String.class,
                Object.class);
    }

    /**
     * Registers a type adapter for custom serialization/deserialization of specific types.
     *
     * @param type    The class type for which to register the adapter
     * @param adapter The adapter instance to handle the type conversion
     */
    public void registerAdapter(Class<?> type, Object adapter) {
        gson = new GsonBuilder().registerTypeAdapter(type, adapter).setPrettyPrinting().create();
    }

    /**
     * Clears all cached JSON data.
     *
     * @return A CompletableFuture that completes when the cache is cleared
     */
    public CompletableFuture<Void> invalidateCache() {
        return cache.clear();
    }

    /**
     * Asynchronously saves an object as JSON to a file.
     *
     * @param plugin The plugin instance
     * @param path   The file path where to save the JSON
     * @param object The object to serialize and save
     * @param <T>    The type of object being saved
     * @return A CompletableFuture that completes when the save operation is finished
     */
    public <T> CompletableFuture<Void> saveJson(Plugin plugin, String path, T object) {
        return CompletableFuture.runAsync(() -> {
            plugin.getDebug().info("json", "Saving " + path + "...");

            File file = FileUtil.createFile(plugin, path);

            try {
                FileUtil.writeFile(plugin, path, gson.toJson(object));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            cache.put(plugin + ":" + path, object);

            plugin.getDebug().info("json", "Saved!");
        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));

    }

    /**
     * Adiciona ou atualiza apenas uma entrada em um JSON de Map<String, T>.
     *
     * @param path O caminho do arquivo JSON
     * @param key  A chave a ser adicionada/atualizada
     * @param value O valor a ser salvo
     * @param <T>  Tipo do valor
     * @return CompletableFuture que completa quando o registro for salvo
     */
    public <T> CompletableFuture<Void> saveEntry(Plugin plugin, String path, String key, T value) {
        return CompletableFuture.runAsync(() -> {
            File file = new File(plugin.getJavaPlugin().getDataFolder(), path);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Type mapType = new TypeToken<java.util.Map<String, Object>>() {}.getType();
            Map<String, Object> map = new HashMap<>();

            try (FileReader reader = new FileReader(file)) {
                Map<String, Object> existing = gson.fromJson(reader, mapType);
                if (existing != null) map.putAll(existing);
            } catch (IOException ignored) {
            }

            map.put(key, value);

            try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
                gson.toJson(map, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));
    }

    /**
     * Asynchronously loads and deserializes a JSON file into an object.
     *
     * @param plugin The plugin instance
     * @param path The path to the JSON file
     * @param clazz The class type to deserialize into
     * @param <T> The type of object being loaded
     * @return A CompletableFuture containing the deserialized object
     */
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> load(Plugin plugin, String path, Class<T> clazz) {
        return (CompletableFuture<T>) CompletableFuture.supplyAsync(() -> {
            if (cache.containsKey(path).join()) {
                return cache.get(path).join();
            }
            File file = FileUtil.createOrSaveResource(plugin, path);

            T t;
            try {
                t = gson.fromJson(new FileReader(file), clazz);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            cache.put(plugin.getName() + ":" + path, t);
            return t;
        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));
    }

    /**
     * Asynchronously loads and deserializes a JSON file into an object using TypeToken.
     * Useful for complex generic types.
     *
     * @param plugin The plugin instance
     * @param path The path to the JSON file
     * @param type The TypeToken representing the type to deserialize into
     * @param <T> The type of object being loaded
     * @return A CompletableFuture containing the deserialized object
     */
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> load(Plugin plugin, String path, TypeToken<T> type) {
        return (CompletableFuture<T>) CompletableFuture.supplyAsync(() -> {
            if (cache.containsKey(path).join()) {
                return cache.get(path).join();
            }
            File file = FileUtil.createOrSaveResource(plugin, path);
            T t;
            try {
                t = gson.fromJson(new FileReader(file), type.getType());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            cache.put(plugin.getName() + ":" + path, t);
            return t;
        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));
    }

    public <T> CompletableFuture<T> loadEntry(Plugin plugin, String path, String key, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(plugin.getJavaPlugin().getDataFolder(), path);
            if (!file.exists()) return null;

            try (FileReader reader = new FileReader(file)) {
                Type mapType = new TypeToken<Map<String, com.google.gson.JsonElement>>() {}.getType();
                Map<String, com.google.gson.JsonElement> map = gson.fromJson(reader, mapType);

                if (map == null) return null;
                com.google.gson.JsonElement element = map.get(key);

                if (element == null) return null;
                return gson.fromJson(element, clazz);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }


}
