package com.xg7plugins.modules.xg7npcs.npc.impl;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

//TODO: Implement DisplayNPC LivingNPC and spawn method
public class DisplayNPC extends NPC {

    private final Consumer<NPCClickEvent> npcClickEventConsumer;
    private final Item startItem;

    public DisplayNPC(Plugin plugin, String id, Hologram hologram, boolean isItemDisplay, Item startItem, Location spawnLocation, Map<EquipmentSlot, Item> equipments, boolean lookAtPlayer, Consumer<NPCClickEvent> npcClickEvent) {
        super(plugin, id, hologram, isItemDisplay ? EntityTypes.ITEM_DISPLAY : EntityTypes.BLOCK_DISPLAY, spawnLocation, equipments, lookAtPlayer);
        this.npcClickEventConsumer = npcClickEvent;
        this.startItem = startItem;
    }

    @Override
    public LivingNPC spawn(Player player) {
        return null;
    }

    @Override
    public void onClick(NPCClickEvent event) {
        if (npcClickEventConsumer != null) {
            npcClickEventConsumer.accept(event);
        }
    }
}
