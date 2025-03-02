package com.xg7plugins.server;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Getter
public enum SoftDependencies {
    PLACEHOLDER_API("PlaceholderAPI"),
    FLOODGATE("floodgate");

    private final String pluginName;
    private boolean installed;

    SoftDependencies(String pluginName) {
        this.pluginName = pluginName;
    }

    public static void initialize() {
        for (SoftDependencies dependency : values()) {
            dependency.installed = Bukkit.getPluginManager().getPlugin(dependency.pluginName) != null;
        }
    }

    public static boolean isGeyserFormsEnabled() {
        return FLOODGATE.installed && Config.mainConfigOf(XG7Plugins.getInstance())
                .get("enable-geyser-forms", Boolean.class)
                .orElse(false);
    }

    public static boolean hasPlaceholderAPI() {
        return PLACEHOLDER_API.installed;
    }
}
