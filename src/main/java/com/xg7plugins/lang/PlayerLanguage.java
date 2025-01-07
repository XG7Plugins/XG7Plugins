package com.xg7plugins.lang;

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
    @Column(name = "lang_id")
    private String langId;

    private PlayerLanguage() {}
}
