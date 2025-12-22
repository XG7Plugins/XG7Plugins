package com.xg7plugins.commands.impl.reload;

import com.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Represents the cause of a reload action within a plugin.
 * <p>
 * This class provides predefined reload causes as well as the ability to register custom causes.
 */
@Getter
@AllArgsConstructor
public class ReloadCause {

    //Custom causes registered by plugins
    private static final HashMap<String, List<ReloadCause>> customCauses = new HashMap<>();

    //Predefined reload causes
    public static final ReloadCause ALL = new ReloadCause("all");
    public static final ReloadCause DATABASE = new ReloadCause("database");
    public static final ReloadCause CONFIG = new ReloadCause("config");
    public static final ReloadCause EVENTS = new ReloadCause("events");
    public static final ReloadCause LANGS = new ReloadCause("langs");
    public static final ReloadCause TASKS = new ReloadCause("tasks");
    public static final ReloadCause EXTENSIONS = new ReloadCause("extensions");

    private final String name;

    public boolean equals(String cause) {
        return name.equalsIgnoreCase("ALL") || name.equalsIgnoreCase(cause);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReloadCause that = (ReloadCause) o;
        return equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /**
     * Registers a custom reload cause for a specific plugin.
     *
     * @param plugin The plugin registering the custom cause
     * @param cause The custom reload cause to register
     */
    public static void registerCause(Plugin plugin, ReloadCause cause) {
        plugin.getDebug().info("load", "Registering reload cause: " + cause.getName() + " for plugin: " + plugin.getName());
        customCauses.putIfAbsent(plugin.getName(), new ArrayList<>());
        customCauses.get(plugin.getName()).add(cause);
    }

    /**
     * Checks if a custom reload cause with the specified name exists for the given plugin.
     * @param plugin The plugin to check for the custom cause
     * @param name The name of the custom reload cause
     * @return True if the custom cause exists, false otherwise
     */
    public static boolean containsCause(Plugin plugin, String name) {
        return customCauses.containsKey(plugin.getName()) && customCauses.get(plugin.getName()).stream().anyMatch(cause -> cause.getName().toUpperCase().equals(name.toUpperCase()));
    }

    /**
     * Retrieves all custom reload causes registered for a specific plugin.
     * @param plugin The plugin to retrieve custom causes for
     * @return A list of custom reload causes for the plugin, or null if none exist
     */
    public static List<ReloadCause> getCausesOf(Plugin plugin) {
        return customCauses.get(plugin.getName());
    }

    /**
     * Retrieves a reload cause by name for a specific plugin.
     * This includes both predefined and custom causes.
     *
     * @param plugin The plugin to retrieve the cause for
     * @param name The name of the reload cause
     * @return The corresponding ReloadCause instance, or null if not found
     */
    public static ReloadCause of(Plugin plugin, String name) {
        switch (name.toUpperCase()) {
            case "ALL":
                return ReloadCause.ALL;
            case "DATABASE":
                return ReloadCause.DATABASE;
            case "CONFIG":
                return ReloadCause.CONFIG;
            case "EVENTS":
                return ReloadCause.EVENTS;
            case "LANGS":
                return ReloadCause.LANGS;
            case "TASKS":
                return ReloadCause.TASKS;
            case "EXTENSIONS":
                return ReloadCause.EXTENSIONS;
            default:
                return customCauses.get(plugin.getName()).stream().filter(cause -> cause.getName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
        }
    }
}
