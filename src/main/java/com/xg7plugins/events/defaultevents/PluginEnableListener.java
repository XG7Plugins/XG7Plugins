package com.xg7plugins.events.defaultevents;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.XG7PluginsPlaceholderExpansion;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginEnableListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("PlaceholderAPI")) {
            Debug.of(XG7Plugins.getInstance()).loading("Registering PlaceholderAPI expansion...");
            new XG7PluginsPlaceholderExpansion().register();
        }
    }
}
