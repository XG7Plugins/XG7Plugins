package com.xg7plugins.data.config;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigTypeAdapter<T> {

    T fromConfig(ConfigurationSection section, Object... optionalArgs);

}
