package com.xg7plugins.data.config;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;

@Getter
public class ConfigManager {

    private final HashMap<String, Config> configs = new HashMap<>();

    public ConfigManager(Plugin plugin, String[] configs) {
        this.configs.put("config", new Config(plugin,"config"));
        this.configs.put("commands", new Config(plugin,"commands"));

        if (configs == null) return;

        Arrays.stream(configs).forEach(config -> this.configs.put(config, new Config(plugin, config)));
    }

    public Config getConfig(String name) {
        return configs.get(name);
    }


}
