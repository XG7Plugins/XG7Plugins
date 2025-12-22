package com.xg7plugins.loader;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main loader class for the XG7Plugins framework.
 * This class extends the classical JavaPlugin and
 * manages the lifecycle of the core plugin.
 */
public final class XG7PluginsLoader extends JavaPlugin {

    @Getter
    private XG7Plugins corePlugin;

    @Override
    public void onLoad() {
        corePlugin = new XG7Plugins(this);
        load(corePlugin);
    }

    @Override
    public void onEnable() {
        enable(corePlugin);
    }

    @Override
    public void onDisable() {
        disable(corePlugin);
    }

    public void load(Plugin plugin) {
        corePlugin.loadPlugin(plugin);
    }

    public void enable(Plugin plugin) {
        corePlugin.enablePlugin(plugin);
    }

    public void disable(Plugin plugin) {
        corePlugin.disablePlugin(plugin);
    }

    public static Plugin getCore() {
        return getPlugin(XG7PluginsLoader.class).getCorePlugin();
    }

}
