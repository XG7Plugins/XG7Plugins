package com.xg7plugins.api.utils;

import com.xg7plugins.api.plugin.Plugin;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PluginLogger {

    private static final boolean LOGGING_ENABLED = true;

    private final Plugin plugin;
    private final Logger logger;
    private Predicate<String> channelFilter;

    public void setChannelFilter(Predicate<String> filter) {
        this.channelFilter = filter != null ? filter : channel -> LOGGING_ENABLED;
    }

    public void log(String message) {
        logger.info("§8[§r" + plugin.getPrefix() + "§8]§r " + message);
    }

    public void info(String channel, String message) {
        if (!channelFilter.test(channel)) return;
        logger.info("§8[§r" + plugin.getPrefix() + " §bINFO§8/§7" + channel + "§8]§r " + message);
    }

    public void warn(String channel, String message) {
        if (!channelFilter.test(channel)) return;
        logger.warning("§8[§r" + plugin.getPrefix() + " §eWARNING§8/§7" + channel + "§8]§e " + message);
    }

    public void severe(String message) {
        logger.severe("§8[§r" + plugin.getPrefix() + " §cERROR§8]§c " + message);
    }

    public void severe(String message, Throwable throwable) {
        logger.log(Level.SEVERE, "§8[§r" + plugin.getPrefix() + " §cERROR§8]§c " + message, throwable);
    }


}
