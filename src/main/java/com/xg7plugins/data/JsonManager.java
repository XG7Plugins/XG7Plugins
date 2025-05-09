package com.xg7plugins.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.managers.Manager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class JsonManager implements Manager {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final ObjectCache<String, Object> cache;
    public JsonManager(XG7Plugins plugin) {
        cache = new ObjectCache<>(
                plugin,
                XG7Plugins.getInstance().getConfigsManager().getConfig("config").getTime("json-cache-expires").orElse(60 * 10 * 1000L),
                false,
                "json-cache",
                false,
                String.class,
                Object.class);
    }

    public void registerAdapter(Class<?> type, Object adapter) {
        gson = new GsonBuilder().registerTypeAdapter(type, adapter).setPrettyPrinting().create();
    }

    public CompletableFuture<Void> invalidateCache() {
        return cache.clear();
    }

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
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));

    }

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
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }
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
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }


}
