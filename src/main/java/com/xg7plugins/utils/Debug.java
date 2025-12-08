package com.xg7plugins.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.text.AnsiParser;
import org.bukkit.Bukkit;

/**
 * Debug utility class for handling different levels of logging messages
 * in the plugin's console output. Controls debug mode and message formatting.
 */
public class Debug {

    private final Plugin plugin;

    private boolean debugEnabled = false;
    private ConfigSection debugSection;

    /**
     * Creates a new Debug instance for the specified plugin.
     * Initializes debug mode from the plugin's configuration.
     *
     * @param plugin The plugin instance this debug logger belongs to
     */
    public Debug(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setupDebugMode() {
        debugSection = ConfigFile.mainConfigOf(plugin).section("debug");

        debugEnabled = debugSection.get("enabled");
        if (plugin instanceof XG7Plugins && PacketEvents.getAPI() != null) {
            PacketEvents.getAPI().getSettings().debug(debugEnabled && ConfigFile.mainConfigOf(plugin).root().get("debug.packet-events", false));
        }
    }

    /**
     * Logs a loading message to the console with the plugin's prefix.
     * This message type is always shown regardless of debug mode.
     *
     * @param message The message to display
     */
    public void log(String message) {
        sendConsole("§8[§r" + plugin.getPrefix() + "§8]§r " + message);
    }

    /**
     * Logs an info message to the console if debug mode is enabled.
     * Uses the plugin's prefix with INFO level indicator.
     *
     * @param message The message to display
     */
    public void info(String logStream, String message) {
        if (!debugEnabled) return;
        if (!debugSection.get(logStream, false)) return;
        sendConsole("§8[§r" + plugin.getPrefix() + " INFO§8]§r " + message);
    }

    /**
     * Logs a warning message to the console if debug mode is enabled.
     * Uses the plugin's prefix with the WARNING level indicator in yellow.
     *
     * @param message The message to display
     */
    public void warn(String logStream, String message) {
        if (!debugEnabled) return;
        if (!debugSection.get(logStream, false)) return;
        sendConsole("§8[§r" + plugin.getPrefix() + " §eWARNING§8]§e " + message);
    }

    /**
     * Logs an error message to the console.
     * This message type is always shown regardless of debug mode.
     * Uses the plugin's prefix with ERROR level indicator in red.
     *
     * @param message The message to display
     */
    public void severe(String message) {
        sendConsole("§8[§r" + plugin.getPrefix() + " §cERROR§8]§c " + message);
    }

    /**
     * Factory method to get the Debug instance for a specific plugin.
     *
     * @param plugin The plugin to get the debug instance for
     * @return The Debug instance associated with the plugin
     */
    public static Debug of(Plugin plugin) {
        return plugin.getDebug();
    }


    private void sendConsole(String msg) {
        Bukkit.getConsoleSender().sendMessage(AnsiParser.parse(msg + "§r"));
    }

}
