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

public class DependencyManager implements Manager {

    private final HashMap<String, Dependency> loadedDependencies = new HashMap<>();

    public boolean isLoaded(String name) {
        return loadedDependencies.containsKey(name) || Bukkit.getPluginManager().isPluginEnabled(name);
    }

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
                return loadpl(dependency, debug, file);
            }
            try {
                debug.loading("Plugin not found, downloading!");
                dependency.downloadDependency();
                debug.loading("Plugin downloaded!");

                return loadpl(dependency, debug, file);

            } catch (Exception e) {
                debug.severe("Error on loading dependency " + dependency.getName() + " " + Arrays.toString(e.getStackTrace()));

                return false;
            }

        } catch (InvalidPluginException | InvalidDescriptionException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean loadpl(Dependency dependency, Debug debug, File file) throws InvalidPluginException, InvalidDescriptionException {
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

    public void loadDependencies(com.xg7plugins.boot.Plugin plugin) {

        List<Dependency> dependencies = plugin.loadDependencies();

        if (dependencies == null) return;


        dependencies.forEach(this::checkDependency);
    }
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
