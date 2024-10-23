package com.xg7plugins.data.lang;

import com.xg7plugins.data.database.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PlayerLanguage implements Entity {

    @Entity.PKey
    private UUID playerUUID;
    private String langId;

    private PlayerLanguage() {}
}
