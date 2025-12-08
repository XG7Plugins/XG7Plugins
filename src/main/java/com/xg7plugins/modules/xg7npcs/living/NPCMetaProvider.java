package com.xg7plugins.modules.xg7npcs.living;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

import java.util.ArrayList;
import java.util.List;

public class NPCMetaProvider {


    public static List<EntityData<?>> getPlayerNPCData() {

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        List<EntityData<?>> data = new ArrayList<>();

        int skinLayersIndex = version.isNewerThanOrEquals(ServerVersion.V_1_17) ? 17 :
                        version.isNewerThanOrEquals(ServerVersion.V_1_13) ? 16 :
                                version.isNewerThanOrEquals(ServerVersion.V_1_9) ? 13 : 10;

        data.add(new EntityData<>(
                skinLayersIndex,
                EntityDataTypes.BYTE,
                (byte) 0x7F
        ));

        return data;
    }


}
