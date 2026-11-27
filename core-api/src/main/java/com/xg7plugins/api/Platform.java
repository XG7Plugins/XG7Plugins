package com.xg7plugins.api;

import com.xg7plugins.api.event.EventBus;
import com.xg7plugins.api.plugin.Plugin;
import com.xg7plugins.api.plugin.ReloadCause;
import com.xg7plugins.api.utils.PluginLogger;

import java.util.concurrent.ConcurrentHashMap;

public interface Platform {

    PlatformType getPlatformType();
    EventBus getEventBus();

    ConcurrentHashMap<Object, Plugin> getPlugins();

    void registerPlugin(Plugin plugin);
    void unregisterPlugin(Plugin plugin);

    void loadPlugin(Plugin plugin);
    void enablePlugin(Plugin plugin);
    void reloadPlugin(Plugin plugin, ReloadCause cause);
    void disablePlugin(Plugin plugin);

}
