package com.xg7plugins.api.plugin;

import com.xg7plugins.api.Platform;
import com.xg7plugins.api.event.Listener;
import com.xg7plugins.api.utils.PluginLogger;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface Plugin {

    String name();
    PluginDescription description();

    Platform getPlatform();
    File getDataFolder();
    Object getHandle();
    PluginLogger getLogger();

    void setEnabled(boolean enabled);
    boolean isEnabled();

    void onPreload();
    void onEnable();
    void onReload(ReloadCause cause);
    void onDisable();

    default List<Listener> loadEvents() {
        return Collections.emptyList();
    }

    default String getPrefix() {
        return description().defaultPrefix();
    }

    default <T> T getHandle(Class<T> type) {
        return type.cast(getHandle());
    }

    default Set<ReloadCause> getCustomReloadCauses() {
        return Collections.emptySet();
    }

    //TODO: The rest implementations

}
