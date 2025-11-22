package com.xg7plugins.api;

import com.xg7plugins.boot.Plugin;

public interface API<T extends Plugin> {

    T getPlugin();

}
