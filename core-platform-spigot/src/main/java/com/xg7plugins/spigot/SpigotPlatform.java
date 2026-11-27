package com.xg7plugins.spigot;

import com.xg7plugins.api.Platform;
import com.xg7plugins.api.PlatformType;
import com.xg7plugins.api.event.EventBus;
import com.xg7plugins.api.plugin.Plugin;
import com.xg7plugins.api.plugin.ReloadCause;
import com.xg7plugins.api.utils.PluginLogger;
import com.xg7plugins.spigot.event.SpigotEventBus;
import com.xg7plugins.spigot.utils.BukkitMetrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class SpigotPlatform implements Platform {

    private final ConcurrentHashMap<Object, Plugin> plugins = new ConcurrentHashMap<>();

    private final SpigotEventBus eventBus = new SpigotEventBus();

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.SPIGOT;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ConcurrentHashMap<Object, Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public void registerPlugin(Plugin plugin) {
        plugins.put(plugin.getHandle(), plugin);
    }

    @Override
    public void unregisterPlugin(Plugin plugin) {
        plugins.remove(plugin.getHandle());
    }

    @Override
    public void loadPlugin(Plugin plugin) {

        //Counts the time taken to load the plugin
        long msLoading = System.currentTimeMillis();
        registerPlugin(plugin);

        PluginLogger logger = plugin.getLogger();

        logger.log("Loading plugin " + plugin.name() + "...");
        plugin.onPreload();

        logger.info("load", "Loading metrics...");
        BukkitMetrics.loadBukkitMetrics(plugin.getHandle(JavaPlugin.class), plugin.description().metricsId());

        if (!plugin.getCustomReloadCauses().isEmpty()) {
            logger.info("load", "Registering custom reload causes...");
            for (ReloadCause cause : plugin.getCustomReloadCauses()) {
                logger.info("load", "Registering reload cause: " + cause.getName() + " for plugin: " + plugin.name());
                ReloadCause.registerCause(plugin, cause);
            }
        }

        //TODO: Dependencies loading
        //TODO: Extensions loading

        plugin.getLogger().log("Plugin " + plugin.getPrefix() + " loaded in " + (System.currentTimeMillis() - msLoading) + "ms.");

    }

    @Override
    public void enablePlugin(Plugin plugin) {

        long msEnabling = System.currentTimeMillis();
        PluginLogger logger = plugin.getLogger();

        logger.log("Enabling plugin " + plugin.getPrefix() + "..." );

        //TODO: Modules initializing

        logger.info("load","Registering listeners...");
        eventBus.registerListeners(plugin);

        plugin.setEnabled(true);

        logger.log("Plugin " + plugin.getPrefix() + " enabled in " + (System.currentTimeMillis() - msEnabling) + "ms.");

    }

    @Override
    public void reloadPlugin(Plugin plugin, ReloadCause cause) {

    }

    @Override
    public void disablePlugin(Plugin plugin) {

        PluginLogger logger = plugin.getLogger();
        logger.log("Disabling " + plugin.getPrefix() + "...");

        plugin.onDisable();

        logger.info("info", "Disabling plugin events...");
        eventBus.unregisterAllListeners(plugin);

        //TODO: Modules disabling

        plugin.setEnabled(false);

    }
}
