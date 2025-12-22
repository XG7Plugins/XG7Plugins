package com.xg7plugins.api;

import com.xg7plugins.boot.Plugin;

/**
 * Generic API interface for XG7Plugins plugins.
 */
public interface API<T extends Plugin> {

    // Returns the plugin instance associated with this API.
    T getPlugin();

}
