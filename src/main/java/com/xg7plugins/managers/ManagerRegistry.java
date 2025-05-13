package com.xg7plugins.managers;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;

public class ManagerRegistry {

    private final Plugin plugin;

    @Getter
    private final ConcurrentHashMap<Class<? extends Manager>, Manager> managers = new ConcurrentHashMap<>();

    public ManagerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerManagers(Manager... managers) {
        for (Manager manager : managers) registerManager(manager);
    }
    public void registerManager(Manager manager) {
        if (plugin.getDebug() != null) plugin.getDebug().loading("Registering manager: " + manager.getClass().getSimpleName());
        else Bukkit.getLogger().info("Registering manager: " + manager.getClass().getSimpleName());
        this.managers.put(manager.getClass(), manager);
    }

    public <T extends Manager> void updateManager(T newManager) {
        managers.put(newManager.getClass(), newManager);
    }

    public <T extends Manager> T getManager(Class<T> tClass) {
        return (T) managers.get(tClass);
    }

    public static <T extends Manager> T get(Plugin plugin, Class<T> tClass) {
        return plugin.getManagerRegistry().getManager(tClass);
    }
}