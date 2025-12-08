package com.xg7plugins.modules.xg7holograms.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.xg7plugins.modules.xg7holograms.hologram.line.impl.TextDisplayLine;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HologramMetadataProvider {

    public static List<EntityData<?>> armorStandData() {

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        List<EntityData<?>> data = triggerArmorStandData();

        if (version.isOlderThan(ServerVersion.V_1_13)) {
            data.add(new EntityData<>(2, EntityDataTypes.STRING, " "));
        } else {
            data.add(new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(Component.text(" "))));
        }

        if (version.isNewerThanOrEquals(ServerVersion.V_1_9)) {
            data.add(new EntityData<>(3, EntityDataTypes.BOOLEAN, true));
            data.add(new EntityData<>(5, EntityDataTypes.BOOLEAN, true));
            return data;
        }

        data.add(new EntityData<>(3, EntityDataTypes.BYTE, (byte) 1));
        data.add(new EntityData<>(5, EntityDataTypes.BYTE, (byte) 1));
        return data;
    }

    public static List<EntityData<?>> triggerArmorStandData() {
        List<EntityData<?>> data = new ArrayList<>();

        data.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20));

        return data;
    }

    public static List<EntityData<?>> updateArmorStandData(Text text) {

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        List<EntityData<?>> data = new ArrayList<>();

        if (version.isOlderThan(ServerVersion.V_1_13)) {
            data.add(new EntityData<>(2, EntityDataTypes.STRING, text.getText()));
        } else {
            data.add(new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(text.toAdventureComponent())));
        }

        if (version.isNewerThanOrEquals(ServerVersion.V_1_9)) {
            data.add(new EntityData<>(3, EntityDataTypes.BOOLEAN, true));
        } else {
            data.add(new EntityData<>(3, EntityDataTypes.BYTE, (byte) 1));
        }

        return data;
    }

    public static List<EntityData<?>> textDisplayData(LivingHologram textDisplayHologram, TextDisplayLine line) {
        List<EntityData<?>> data = new ArrayList<>();

        data.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, line.getScale()));

        data.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT,
                Text.detectLangs(textDisplayHologram.getPlayer(),
                        textDisplayHologram.getHologram().getPlugin(),
                        line.getLine()).join().toAdventureComponent()));

        data.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) line.getBillboard().ordinal()));

        byte flags = 0;
        if (line.isShadow()) flags |= 0x01;
        if (line.isSeeThrough()) flags |= 0x02;
        switch (line.getAlignment()) {
            case LEFT:
                flags |= 0x08;
                break;
            case RIGHT:
                flags |= 0x08 * 2;
                break;
        }

        data.add(new EntityData<>(27, EntityDataTypes.BYTE, flags));

        if (line.isBackground()) {
            int argb = (line.getBackgroundColor().getAlpha() << 24)
                    | (line.getBackgroundColor().getRed() << 16)
                    | (line.getBackgroundColor().getGreen() << 8)
                    | (line.getBackgroundColor().getBlue());

            data.add(new EntityData<>(25, EntityDataTypes.INT, argb));
        } else {
            data.add(new EntityData<>(25, EntityDataTypes.INT, 0x00000000));
        }

        return data;
    }

    public static List<EntityData<?>> textDisplayUpdateData(Text line) {
        List<EntityData<?>> data = new ArrayList<>();

        data.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, line.toAdventureComponent()));

        return data;
    }
}
