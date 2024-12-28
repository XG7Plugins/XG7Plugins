package com.xg7plugins.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class JsonManager {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Cache<String, Object> cache = Caffeine.newBuilder().expireAfterAccess(XG7Plugins.getInstance().getConfigsManager().getConfig("config").getTime("json-cache-expires").orElse(60 * 10 * 1000L), TimeUnit.MINUTES).build();

    public void registerAdapter(Class<?> type, Object adapter) {
        gson = new GsonBuilder().registerTypeAdapter(type, adapter).setPrettyPrinting().create();
    }

    public void invalidateCache() {
        cache.invalidateAll();
    }

    public <T> CompletableFuture<Void> saveJson(Plugin plugin, String path, T object) {
        return CompletableFuture.runAsync(() -> {
            plugin.getLog().info("Saving " + path + "...");

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
                plugin.getLog().severe("Erro ao serializar o objeto: " + e.getMessage());
                e.printStackTrace();
            }

            cache.put(path, object);

            plugin.getLog().info("Saved!");
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));

    }


    public <T> CompletableFuture<T> load(Plugin plugin, String path, Class<T> clazz) {
        if (cache.asMap().containsKey(path)) return CompletableFuture.completedFuture((T) cache.getIfPresent(path));
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(plugin.getDataFolder(), path);
            if (!file.exists()) saveJson(plugin, path, new Object()).join();
            T t;
            try {
                t = gson.fromJson(new FileReader(file), clazz);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            cache.put(path, t);
            return t;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }


}
