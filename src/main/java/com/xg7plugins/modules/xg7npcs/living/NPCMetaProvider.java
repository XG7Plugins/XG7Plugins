package com.xg7plugins.modules.xg7npcs.living;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.hologram.line.impl.TextDisplayLine;
import com.xg7plugins.modules.xg7npcs.living.impl.LivingDisplayNPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.DisplayNPC;
import com.xg7plugins.utils.EntityDisplayOptions;
import com.xg7plugins.utils.text.Text;

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

    public static List<EntityData<?>> entityDisplayData(LivingDisplayNPC livingDisplayNPC) {
        List<EntityData<?>> data = new ArrayList<>();

        DisplayNPC displayNPC = (DisplayNPC) livingDisplayNPC.getNPC();

        EntityDisplayOptions options = displayNPC.getDisplayOptions();

        data.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, options.getScale()));

        data.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) options.getBillboard().ordinal()));


        if (displayNPC.getEntityType() == EntityTypes.BLOCK_DISPLAY) {
            data.add(new EntityData<>(23, EntityDataTypes.BLOCK_STATE, WrappedBlockState.getDefaultState(StateTypes.getByName(displayNPC.getStartItem().toProtocolItemStack().getType().getName())).getGlobalId()));
        }

        if  (displayNPC.getEntityType() == EntityTypes.ITEM_DISPLAY) {
            data.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK, displayNPC.getStartItem().toProtocolItemStack()));

        }

        return data;
    }


}
