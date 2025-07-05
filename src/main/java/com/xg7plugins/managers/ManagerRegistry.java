package com.xg7plugins.managers;

import com.xg7plugins.boot.Plugin;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the registration and retrieval of manager instances in a plugin.
 * Provides a thread-safe storage using ConcurrentHashMap to maintain manager instances
 * and allows access to them through their respective class types.
 */
public class ManagerRegistry {

    /**
     * The plugin instance that owns this registry
     */
    private final Plugin plugin;

    /**
     * Thread-safe map storing all registered managers, indexed by their class type
     */
    @Getter
    private final ConcurrentHashMap<Class<? extends Manager>, Manager> managers = new ConcurrentHashMap<>();

    /**
     * Creates a new ManagerRegistry for the specified plugin.
     *
     * @param plugin The plugin instance that owns this registry
     */
    public ManagerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers multiple manager instances at once.
     *
     * @param managers Variable number of managers to register
     */
    public void registerManagers(Manager... managers) {
        for (Manager manager : managers) registerManager(manager);
    }

    /**
     * Registers a single manager instance and logs the registration.
     * Uses debug logging if available, otherwise uses Bukkit's default logger.
     *
     * @param manager The manager instance to register
     */
    public void registerManager(Manager manager) {
        if (manager == null) return;
        if (plugin.getDebug() != null) plugin.getDebug().loading("Registering manager: " + manager.getClass().getSimpleName());
        else Bukkit.getLogger().info("Registering manager: " + manager.getClass().getSimpleName());
        this.managers.put(manager.getClass(), manager);
    }

    /**
     * Updates or replaces an existing manager instance with a new one.
     *
     * @param newManager The new manager instance to use
     * @param <T>        The type of manager being updated
     */
    public <T extends Manager> void updateManager(T newManager) {
        managers.put(newManager.getClass(), newManager);
    }

    /**
     * Retrieves a manager instance by its class type.
     *
     * @param tClass The class of the manager to retrieve
     * @param <T>    The type of manager to return
     * @return The manager instance of the specified type
     */
    public <T extends Manager> T getManager(Class<T> tClass) {
        return (T) managers.get(tClass);
    }

    /**
     * Static utility method to directly get a manager instance from a plugin.
     *
     * @param plugin The plugin containing the manager registry
     * @param tClass The class of the manager to retrieve
     * @param <T>    The type of manager to return
     * @return The manager instance of the specified type
     */
    public static <T extends Manager> T get(Plugin plugin, Class<T> tClass) {
        return plugin.getManagerRegistry().getManager(tClass);
    }
}