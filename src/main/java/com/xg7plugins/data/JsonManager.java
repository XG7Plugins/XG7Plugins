package com.xg7plugins.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class JsonManager {

    private final XG7Plugins plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    private final Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    public void saveJson(Plugin plugin, String path, Object object) throws IOException {
        plugin.getLog().info("Saving " + path + ".json...");


        File file = new File(plugin.getDataFolder(), path);

        file.getParentFile().mkdirs();

        if (!file.exists()) file.createNewFile();

        Writer writer = new FileWriter(file, false);
        gson.toJson(object, writer);
        writer.flush();
        writer.close();

        plugin.getLog().info("Saved!");
    }

    public <T> T load(String path, Class<T> clazz) throws IOException {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) return null;
        T t = gson.fromJson(new FileReader(file), clazz);
        cache.put(path, t);
        return t;
    }


}
