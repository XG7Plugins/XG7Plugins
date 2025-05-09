package com.xg7plugins.dependencies;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.Debug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DependencyManager implements Manager {

    private final HashMap<String, Dependency> loadedDependencies = new HashMap<>();

    public boolean isLoaded(String name) {
        return loadedDependencies.containsKey(name) || Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public void loadDependencies(Dependency... dependencies) {

        Debug debug = Debug.of(XG7Plugins.getInstance());

        Arrays.stream(dependencies).forEach(d -> {
            debug.loading("Loading dependency: " + d.getName());
            if (Bukkit.getPluginManager().isPluginEnabled(d.getName())) {
                debug.loading("Dependency already loaded");
                loadedDependencies.put(d.getName(), d);
                return;
            }
            CompletableFuture.runAsync(() -> {
                debug.loading("Loading from file (async)");
                File file = new File(XG7Plugins.getInstance().getDataFolder().getParentFile(), d.getName());

                try {
                    Plugin loaded = Bukkit.getPluginManager().loadPlugin(file);

                    if (loaded != null) {
                        Bukkit.getPluginManager().enablePlugin(loaded);
                        debug.loading("Loaded!");
                        loadedDependencies.put(d.getName(), d);
                        return;
                    }
                    try {
                        debug.loading("Plugin not found, downloading!");
                        d.downloadDependency();
                        debug.loading("Plugin downlaoded!");

                        Plugin newDownloaded = Bukkit.getPluginManager().loadPlugin(file);

                        if (newDownloaded != null) {
                            Bukkit.getPluginManager().enablePlugin(newDownloaded);
                            loadedDependencies.put(d.getName(), d);
                            return;
                        }

                        debug.severe("Error on loading dependency " + d.getName());
                    } catch (Exception e) {
                        debug.severe("Error on loading dependency " + d.getName() + " " + Arrays.toString(e.getStackTrace()));
                        e.printStackTrace();
                    }

                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
    public boolean loadRequiredDependencies(Plugin plugin, Dependency... dependencies) {
        Debug debug = Debug.of(XG7Plugins.getInstance());

        boolean error = false;

        for (Dependency d : dependencies) {
            debug.loading("Loading required dependency: " + d.getName());
            if (Bukkit.getPluginManager().isPluginEnabled(d.getName())) {
                debug.loading("Dependency already loaded");
                loadedDependencies.put(d.getName(), d);

                continue;
            }
            debug.loading("Loading from file");
            File file = new File(XG7Plugins.getInstance().getDataFolder().getParentFile(), d.getName());

            try {
                Plugin loaded = Bukkit.getPluginManager().loadPlugin(file);

                if (loaded != null) {
                    Bukkit.getPluginManager().enablePlugin(loaded);
                    debug.loading("Loaded!");
                    loadedDependencies.put(d.getName(), d);
                    continue;
                }
                try {
                    debug.loading("Plugin not found, downloading!");
                    d.downloadDependency();
                    debug.loading("Plugin downlaoded!");

                    Plugin newDownloaded = Bukkit.getPluginManager().loadPlugin(file);

                    if (newDownloaded != null) {
                        Bukkit.getPluginManager().enablePlugin(newDownloaded);
                        debug.loading("Loaded!");
                        loadedDependencies.put(d.getName(), d);
                        continue;
                    }

                    debug.severe("Error on loading dependency " + d.getName() + " disabling plugin...");


                    error = true;

                } catch (Exception e) {
                    debug.severe("Error on loading dependency " + d.getName() + " disabling plugin... " + Arrays.toString(e.getStackTrace()));

                    error = true;
                }

            } catch (InvalidPluginException | InvalidDescriptionException e) {
                throw new RuntimeException(e);
            }
        }

        return error;
    }

}
