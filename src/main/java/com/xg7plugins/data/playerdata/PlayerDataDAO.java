package com.xg7plugins.data.playerdata;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.DAO;

import java.util.UUID;

public class PlayerDataDAO implements DAO<UUID, PlayerData> {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public Class<PlayerData> getEntityClass() {
        return PlayerData.class;
    }
}
