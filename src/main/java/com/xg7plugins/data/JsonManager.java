package com.xg7plugins.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class JsonManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterAccess(XG7Plugins.getInstance().getConfigsManager().getConfig("config").getTime("json-cache-expires"), TimeUnit.MINUTES).build();

    public <T> void saveJson(Plugin plugin, String path, T object) throws IOException {
        plugin.getLog().info("Saving " + path + "...");

        // Verifica se o diretório existe, caso contrário cria
        File file = new File(plugin.getDataFolder(), path);
        if (!file.getParentFile().exists()) {
            plugin.getLog().info("Diretório não existe, criando...");
            file.getParentFile().mkdirs();
        }

        // Cria o arquivo caso não exista
        if (!file.exists()) {
            plugin.getLog().info("Arquivo não existe, criando...");
            file.createNewFile();
        } else {
            plugin.getLog().info("Arquivo já existe, sobrescrevendo...");
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
            gson.toJson(object, writer);
        } catch (Exception e) {
            plugin.getLog().severe("Erro ao serializar o objeto: " + e.getMessage());
            throw e;
        }

        plugin.getLog().info("Saved!");
    }


    public <T> T load(Plugin plugin, String path, Class<T> clazz) throws IOException {
        if (cache.getIfPresent(path) != null) return (T) cache.getIfPresent(path);
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) saveJson(plugin, path, new Object());
        T t = gson.fromJson(new FileReader(file), clazz);
        cache.put(path, t);
        return t;
    }


}
