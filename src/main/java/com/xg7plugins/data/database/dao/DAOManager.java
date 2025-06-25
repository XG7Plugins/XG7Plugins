package com.xg7plugins.data.database.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.managers.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DAOManager implements Manager {

    private final HashMap<Class<? extends DAO>, DAO> daoMap = new HashMap<>();

    public void registerDAO(DAO dao) {
        daoMap.put(dao.getClass(), dao);
    }

    public  <ID,T extends Entity<?, ?>, U extends DAO<ID,T>> U getDAO(Class<U> daoClass) {
        return daoClass.cast(daoMap.get(daoClass));
    }

    public List<DAO> getAllDAOs() {
        return new ArrayList<>(daoMap.values());
    }

    public List<DAO>getAllDAOsByPlugin(Plugin plugin) {
        return daoMap.values().stream().filter(dao -> dao.getPlugin().getName().equalsIgnoreCase(plugin.getName())).collect(Collectors.toList());
    }







}
