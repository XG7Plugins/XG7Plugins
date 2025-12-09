package com.xg7plugins.utils;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class PluginKey {

    private final Plugin plugin;
    private final String key;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PluginKey)) {
            return false;
        }

        PluginKey other = (PluginKey) obj;

        return this.plugin.getName().equals(other.plugin.getName()) && this.key.equals(other.key);
    }

    @Override
    public String toString() {
        return (plugin.getName() + ":" + key).toLowerCase();
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin.getName(), key);
    }

    public static PluginKey of(Plugin plugin, String key) {
        return new PluginKey(plugin, key);
    }

    public static PluginKey of(String pluginKey) {
        if (!pluginKey.toLowerCase().equals(pluginKey)) {
            throw new IllegalArgumentException("This is not a valid plugin key");
        }

        String[] split = pluginKey.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("This is not a valid plugin key");
        }

        Plugin plugin = XG7Plugins.getAPI().getXG7Plugin(split[0]);

        if (plugin == null) {
            throw new IllegalArgumentException("This is not a valid plugin in that key");
        }

        String key = split[1];

        return new PluginKey(plugin, key);
    }

}
