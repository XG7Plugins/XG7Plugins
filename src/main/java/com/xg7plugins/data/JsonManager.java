package com.xg7plugins.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class JsonManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Cache<String, Object> cache = Caffeine.newBuilder().expireAfterAccess(XG7Plugins.getInstance().getConfigsManager().getConfig("config").getTime("json-cache-expires"), TimeUnit.MINUTES).build();

    public <T> void saveJson(Plugin plugin, String path, T object) throws IOException {
        plugin.getLog().info("Saving " + path + "...");

        File file = new File(plugin.getDataFolder(), path);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();


        try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
            gson.toJson(object, writer);
        } catch (Exception e) {
            plugin.getLog().severe("Erro ao serializar o objeto: " + e.getMessage());
            throw e;
        }

        plugin.getLog().info("Saved!");
    }


    public <T> T load(Plugin plugin, String path, Class<T> clazz) throws IOException {
        if (cache.asMap().containsKey(path)) return (T) cache.getIfPresent(path);
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) saveJson(plugin, path, new Object());
        T t = gson.fromJson(new FileReader(file), clazz);
        cache.put(path, t);
        return t;
    }


}
