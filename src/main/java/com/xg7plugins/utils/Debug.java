package com.xg7plugins.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 * This class is used to debug
 */
@AllArgsConstructor
public class Debug {

    private Plugin plugin;
    @Setter
    private boolean debugEnabled;
    private Config config;

    public Debug(Plugin plugin) {
        this.plugin = plugin;
        Config config = Config.mainConfigOf(plugin);
        debugEnabled = config.get("debug.enabled", Boolean.class).orElse(false);
        if (plugin instanceof XG7Plugins) {
            if (debugEnabled && config.get("debug.packet-events", Boolean.class).orElse(false)) PacketEvents.getAPI().getSettings().debug(true);
        }
        this.config = config;
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "§8]§r " + message);
    }
    public void info(String condition, String message) {
        if (!config.get("debug." + condition, Boolean.class).orElse(false)) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "INFO §8]§r " + message);
    }
    public void warn(String condition, String message) {
        if (!config.get("debug." + condition, Boolean.class).orElse(false)) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "WARNING §8]§e " + message);
    }
    public void severe(String condition, String message) {
        if (!config.get("debug." + condition, Boolean.class).orElse(false)) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "ERROR §8]§c " + message);
    }
    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "ERROR §8]§c " + message);
    }
    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "LOG §8]§r " + message);
    }

}
