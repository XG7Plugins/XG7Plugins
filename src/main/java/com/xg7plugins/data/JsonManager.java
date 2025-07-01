package com.xg7plugins.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/**
 * Manages JSON data operations including saving, loading, and caching.
 * Provides functionality for serializing/deserializing objects to/from JSON format
 * with support for type adapters and caching mechanism.
 */
public class JsonManager implements Manager {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final ObjectCache<String, Object> cache;
    public JsonManager(XG7Plugins plugin) {
        cache = new ObjectCache<>(
                plugin,
                Config.mainConfigOf(plugin).getTimeInMilliseconds("json-cache-expires").orElse(60 * 10 * 1000L),
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
            plugin.getDebug().info("Saving " + path + "...");

            File file = new File(plugin.getDataFolder(), path);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


            try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
                gson.toJson(object, writer);
            } catch (Exception e) {
                plugin.getDebug().severe("Error on object serialize: " + e.getMessage());
                e.printStackTrace();
            }

            cache.put(plugin + ":" + path, object);

            plugin.getDebug().info("Saved!");
        }, XG7PluginsAPI.taskManager().getExecutor("files"));

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
            File file = new File(plugin.getDataFolder(), path);
            if (!file.exists()) plugin.saveResource(path, false);
            T t;
            try {
                t = gson.fromJson(new FileReader(file), clazz);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            cache.put(plugin.getName() + ":" + path, t);
            return t;
        }, XG7PluginsAPI.taskManager().getExecutor("files"));
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
            File file = new File(plugin.getDataFolder(), path);
            if (!file.exists()) plugin.saveResource(path, false);
            T t;
            try {
                t = gson.fromJson(new FileReader(file), type.getType());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            cache.put(plugin.getName() + ":" + path, t);
            return t;
        }, XG7PluginsAPI.taskManager().getExecutor("files"));
    }


}
