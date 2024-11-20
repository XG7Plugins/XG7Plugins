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
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix()  + " §cERROR§8] §c" + message);
    }

    public void fine(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix()  + " §aSUCCESS§8] §a" + message);
    }

    public void info(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + " §6DEBUG§8] §6" + message);
    }

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + " §eALERT§8] §e" + message);
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getCustomPrefix() + "§8] §r" + message);
    }

}
