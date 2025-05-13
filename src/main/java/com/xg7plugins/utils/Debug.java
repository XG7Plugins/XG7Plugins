package com.xg7plugins.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.HashMap;

/**
 * This class is used to debug
 */
public class Debug {

    private final Plugin plugin;
    @Setter
    private boolean debugEnabled;

    public Debug(Plugin plugin) {
        this.plugin = plugin;
        Config config = Config.mainConfigOf(plugin);
        debugEnabled = config.getConfig().getBoolean("debug-enabled");
        PacketEvents.getAPI().getSettings().debug(plugin instanceof XG7Plugins && debugEnabled);
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getEnvironmentConfig().getPrefix() + "§8]§r " + message);
    }
    public void info(String message) {
        if (!debugEnabled) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getEnvironmentConfig().getPrefix()+ " INFO§8]§r " + message);
    }
    public void warn(String message) {
        if (!debugEnabled) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getEnvironmentConfig().getPrefix() + " §eWARNING§8]§e " + message);
    }
    public void severe(String message) {
        if (!debugEnabled) return;
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getEnvironmentConfig().getPrefix() + " §cERROR§8]§c " + message);
    }
    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getEnvironmentConfig().getPrefix() + " LOG§8]§r " + message);
    }

    public static Debug of(Plugin plugin) {
        return plugin.getDebug();
    }

}
