package com.xg7plugins.data.database.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Entity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages database repositories for entities.
 * This class handles registration and retrieval of repositories that interact with the database.
 * interface as part of the plugin's management system.
 */
public class RepositoryManager {

    /**
     * Map storing repository instances mapped to their class types
     */
    private final HashMap<Class<? extends Repository>, Repository> daoMap = new HashMap<>();

    /**
     * Registers a repository instance to be managed by this class
     *
     * @param dao The repository instance to register
     */
    public void registerRepository(Repository dao) {
        daoMap.put(dao.getClass(), dao);
    }

    /**
     * Gets a repository instance by its class type
     *
     * @param daoClass The class of the repository to retrieve
     * @param <ID>     The type of ID used by the repository's entities
     * @param <T>      The type of entities managed by the repository
     * @param <U>      The type of repository to retrieve
     * @return The repository instance cast to the requested type
     */
    public <ID, T extends Entity<?, ?>, U extends Repository<ID, T>> U getRepository(Class<U> daoClass) {
        return daoClass.cast(daoMap.get(daoClass));
    }

    /**
     * Gets a list of all registered repositories
     *
     * @return List of all repositories
     */
    public List<Repository> getAllRepositories() {
        return new ArrayList<>(daoMap.values());
    }

    /**
     * Gets all repositories registered for a specific plugin
     *
     * @param plugin The plugin to get repositories for
     * @return List of repositories belonging to the specified plugin
     */
    public List<Repository> getAllRepositoriesByPlugin(Plugin plugin) {
        return daoMap.values().stream().filter(dao -> dao.getPlugin().getName().equalsIgnoreCase(plugin.getName())).collect(Collectors.toList());
    }

}