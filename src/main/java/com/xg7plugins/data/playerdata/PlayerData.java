package com.xg7plugins.data.playerdata;

import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Table(name = "player_data")
public class PlayerData implements Entity<UUID, PlayerData> {

    @Pkey
    @Column(name = "player_id")
    private UUID playerUUID;
    @Column(name = "first_join")
    private Long firstJoin;
    @Column(name = "lang_id")
    private String langId;

    private PlayerData() {}

    public PlayerData(UUID playerUUID, String langId) {
        this.playerUUID = playerUUID;
        this.langId = langId;
        this.firstJoin = System.currentTimeMillis();
    }

    @Override
    public boolean equals(PlayerData other) {
        return playerUUID.equals(other.playerUUID);
    }

    @Override
    public UUID getID() {
        return playerUUID;
    }
}
