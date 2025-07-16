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

/**
 * Manages plugin dependencies by handling their loading, checking, and downloading.
 * This class is responsible for ensuring all required dependencies are available
 * and properly loaded before plugin initialization.
 */
public class DependencyManager implements Manager {

    private final HashMap<String, Dependency> loadedDependencies = new HashMap<>();

    /**
     * Checks if a dependency is already loaded.
     *
     * @param name The name of the dependency to check
     * @return true if the dependency is loaded, false otherwise
     */
    public boolean isLoaded(String name) {
        return loadedDependencies.containsKey(name) || Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public boolean exists(String name) {
        return isLoaded(name) || Bukkit.getPluginManager().getPlugin(name) != null;
    }

    /**
     * Checks and loads a dependency if it's not already loaded.
     * Downloads the dependency if it's not found locally.
     *
     * @param dependency The dependency to check and load
     * @return true if the dependency was loaded successfully, false otherwise
     */
    private boolean checkDependency(Dependency dependency) {

        Debug debug = Debug.of(XG7Plugins.getInstance());

        debug.loading("Loading dependency: " + dependency.getName());
        if (Bukkit.getPluginManager().isPluginEnabled(dependency.getName())) {
            debug.loading("Dependency already loaded");
            loadedDependencies.put(dependency.getName(), dependency);

            return true;
        }
        debug.loading("Loading from file");
        File file = new File(XG7Plugins.getInstance().getDataFolder().getParentFile(), dependency.getName() + ".jar");

        try {
            if (file.exists()) {
                return loadPl(dependency, debug, file);
            }
            try {
                debug.loading("Plugin not found, downloading!");
                dependency.downloadDependency();
                debug.loading("Plugin downloaded!");

                return loadPl(dependency, debug, file);

            } catch (Exception e) {
                debug.severe("Error on loading dependency " + dependency.getName() + " " + Arrays.toString(e.getStackTrace()));

                return false;
            }

        } catch (InvalidPluginException | InvalidDescriptionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a plugin from a file and enables it.
     *
     * @param dependency The dependency to load
     * @param debug      Debug instance for logging
     * @param file       The plugin file to load
     *
     * @return true if the plugin was loaded successfully, false otherwise
     *
     * @throws InvalidPluginException      if the plugin is invalid
     * @throws InvalidDescriptionException if the plugin description is invalid
     */
    private boolean loadPl(Dependency dependency, Debug debug, File file) throws InvalidPluginException, InvalidDescriptionException {
        Plugin loaded = Bukkit.getPluginManager().loadPlugin(file);

        if (loaded != null) {
            Bukkit.getPluginManager().enablePlugin(loaded);
            debug.loading("Loaded!");
            loadedDependencies.put(dependency.getName(), dependency);
            return true;
        }
        debug.severe("Error on loading dependency " + dependency.getName());
        return false;
    }

    /**
     * Loads all dependencies for a given plugin.
     *
     * @param plugin The plugin whose dependencies need to be loaded
     */
    public void loadDependencies(com.xg7plugins.boot.Plugin plugin) {

        List<Dependency> dependencies = plugin.loadDependencies();

        if (dependencies == null) return;


        dependencies.forEach(this::checkDependency);
    }

    /**
     * Loads all required dependencies for a given plugin.
     * Stops if any required dependency fails to load.
     *
     * @param plugin The plugin whose required dependencies need to be loaded
     * @return true if all required dependencies were loaded successfully, false otherwise
     */
    public boolean loadRequiredDependencies(com.xg7plugins.boot.Plugin plugin) {
        List<Dependency> dependencies = plugin.loadRequiredDependencies();

        if (dependencies == null) return true;

        for (Dependency dependency : dependencies) {
            if (!checkDependency(dependency)) {
                return false;
            }
        }
        return true;
    }

}
