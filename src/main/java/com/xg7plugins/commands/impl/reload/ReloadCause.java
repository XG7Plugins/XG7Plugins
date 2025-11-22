package com.xg7plugins.commands.impl.reload;

import com.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class ReloadCause {

    private static final HashMap<String, List<ReloadCause>> customCauses = new HashMap<>();

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

    public static void registerCause(Plugin plugin, ReloadCause cause) {
        plugin.getDebug().info("load", "Registering reload cause: " + cause.getName() + " for plugin: " + plugin.getName());
        customCauses.putIfAbsent(plugin.getName(), new ArrayList<>());
        customCauses.get(plugin.getName()).add(cause);
    }

    public static boolean containsCause(Plugin plugin, String name) {
        return customCauses.containsKey(plugin.getName()) && customCauses.get(plugin.getName()).stream().anyMatch(cause -> cause.getName().toUpperCase().equals(name.toUpperCase()));
    }

    public static List<ReloadCause> getCausesOf(Plugin plugin) {
        return customCauses.get(plugin.getName());
    }


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
