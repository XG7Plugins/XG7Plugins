package com.xg7plugins.utils;

import com.xg7plugins.boot.Plugin;
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
        isLogEnabled = config.get("log-enabled", boolean.class).orElse(false);
    }

    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix()  + " §cERROR§8] §c" + message);
    }

    public void fine(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix()  + " §aSUCCESS§8] §a" + message);
    }

    public void info(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix() + " §6DEBUG§8] §6" + message);
    }

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix() + " §eALERT§8] §e" + message);
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix() + "§8] §r" + message);
    }
    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("§8[§r" + plugin.getPrefix() + " §cLOG§8] §r" + message);
    }

}
