package com.xg7plugins.data.playerdata;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.dao.Repository;

import java.util.UUID;

public class PlayerDataRepository implements Repository<UUID, PlayerData> {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public Class<PlayerData> getEntityClass() {
        return PlayerData.class;
    }
}
