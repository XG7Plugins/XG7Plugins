package com.xg7plugins.modules.xg7npcs.npc.impl;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.living.impl.LivingDisplayNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.utils.EntityDisplayOptions;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

//TODO: Implement DisplayNPC LivingNPC and spawn method
public class DisplayNPC extends NPC {

    private final Consumer<NPCClickEvent> npcClickEventConsumer;

    @Getter
    private final Item startItem;
    @Getter
    private final EntityDisplayOptions displayOptions;

    public DisplayNPC(Plugin plugin, String id, Hologram hologram, boolean isItemDisplay, Item startItem, EntityDisplayOptions displayOptions, Location spawnLocation, Map<EquipmentSlot, Item> equipments, boolean lookAtPlayer, boolean glow, Consumer<NPCClickEvent> npcClickEvent) {
        super(plugin, id, hologram, isItemDisplay ? EntityTypes.ITEM_DISPLAY : EntityTypes.BLOCK_DISPLAY, spawnLocation, equipments, lookAtPlayer, glow);
        this.npcClickEventConsumer = npcClickEvent;
        this.startItem = startItem;
        this.displayOptions = displayOptions;
    }

    @Override
    public LivingNPC spawn(Player player) {
        LivingDisplayNPC displayNPC = new LivingDisplayNPC(player, this);
        displayNPC.spawn();
        XG7Plugins.getAPI().npcs().registerLivingNPC(displayNPC);
        return displayNPC;
    }

    @Override
    public void onClick(NPCClickEvent event) {
        if (npcClickEventConsumer != null) {
            npcClickEventConsumer.accept(event);
        }
    }
}
