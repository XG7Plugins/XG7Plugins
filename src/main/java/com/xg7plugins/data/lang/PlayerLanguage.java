package com.xg7plugins.data.lang;

import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Table(name = "languages")
public class PlayerLanguage implements Entity {

    @Pkey
    @Column(name = "player_id")
    private UUID playerUUID;
    private String langId;

    private PlayerLanguage() {}
}
