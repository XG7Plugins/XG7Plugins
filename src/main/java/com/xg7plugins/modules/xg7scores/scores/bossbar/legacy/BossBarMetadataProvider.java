package com.xg7plugins.modules.xg7scores.scores.bossbar.legacy;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityMetadataProvider;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class BossBarMetadataProvider implements EntityMetadataProvider {

    private final float healthPercent;
    private final List<String> updateText;

    @Override
    public @NotNull List<EntityData<?>> entityData(ClientVersion version) {

        List<EntityData<?>> entityData = new ArrayList<>();

        entityData.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) (1 << 5)));

        entityData.add(new EntityData<>(2, EntityDataTypes.STRING, updateText.get(0)));

        entityData.add(new EntityData<>(3, EntityDataTypes.BYTE, (byte) 1));
        entityData.add(new EntityData<>(11, EntityDataTypes.BYTE, (byte) 1));

        entityData.add(new EntityData<>(6, EntityDataTypes.FLOAT, (healthPercent / 100) * 300));

        entityData.add(new EntityData<>(17, EntityDataTypes.INT, 0));
        entityData.add(new EntityData<>(18, EntityDataTypes.INT, (0)));
        entityData.add(new EntityData<>(19, EntityDataTypes.INT, 0));

        entityData.add(new EntityData<>(20, EntityDataTypes.INT, 1000));

        return entityData;
    }
}
