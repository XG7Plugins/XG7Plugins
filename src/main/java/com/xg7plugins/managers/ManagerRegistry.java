package com.xg7plugins.managers;

import com.xg7plugins.boot.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class ManagerRegistry {

    private final Plugin plugin;

    private final ConcurrentHashMap<Class<? extends Manager>, Manager> managers = new ConcurrentHashMap<>();

    public ManagerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerManagers(Manager... managers) {
        for (Manager manager : managers) {
            plugin.getDebug().loading("Registering manager:" + manager.getClass().getSimpleName());
            this.managers.put(manager.getClass(), manager);
        }
    }

    public <T extends Manager> void updateManager(T newManager) {
        managers.put(newManager.getClass(), newManager);
    }

    public <T extends Manager> T getManager(Class<T> tClass) {
        return (T) managers.get(tClass);
    }

    public static <T extends Manager> T get(Plugin plugin, Class<T> tClass) {
        return plugin.getManagerRegistery().getManager(tClass);
    }
}