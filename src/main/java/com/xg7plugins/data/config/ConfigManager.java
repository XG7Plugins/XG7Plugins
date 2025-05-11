package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.managers.Manager;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

@Getter
public class ConfigManager implements Manager {

    private final HashMap<String, Config> configs = new HashMap<>();
    private final HashMap<Class<?>, ConfigTypeAdapter<?>> adapters = new HashMap<>();

    public ConfigManager(Plugin plugin, String[] configs) {

        plugin.getLogger().log(Level.CONFIG, "Loading configs of " + plugin.getName());

        putConfig("config", new Config(plugin,"config"));
        putConfig("commands", new Config(plugin,"commands"));

        if (configs == null) return;

        Arrays.stream(configs).forEach(config -> putConfig(config, new Config(plugin, config)));
    }

    public void reloadConfigs() {
        configs.values().forEach(Config::reload);
    }

    public void putConfig(String name, Config config) {
        configs.put(name, config);
    }

    public Config getConfig(String name) {
        return configs.get(name);
    }

    public <T> void registerAdapter(Class<T> tClass, ConfigTypeAdapter<T> adapter) {
        adapters.put(tClass, adapter);
    }


}
