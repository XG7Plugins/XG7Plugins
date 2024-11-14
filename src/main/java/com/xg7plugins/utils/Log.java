package com.xg7plugins.utils;

import com.xg7plugins.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 * This class is used to debug
 */
@AllArgsConstructor
public class Log {

    private Plugin plugin;
    @Setter
    private boolean isLogEnabled;

    public Log(Plugin plugin) {
        this.plugin = plugin;
        Config config = plugin.getConfigsManager().getConfig("config");
        if (config == null) return;
        if (config.get("log-enabled") == null) return;
        isLogEnabled = config.get("log-enabled");
    }

    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage("§c[" + plugin.getName()  + " ERROR] " + message);
    }

    public void fine(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§a[" + plugin.getName()  + " SUCCESS] " + message);
    }

    public void info(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§6[" + plugin.getName() + " DEBUG] " + message);
    }

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§e[" + plugin.getName() + " ALERT] " + message);
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage(plugin.getCustomPrefix() + " " + message);
    }

}
