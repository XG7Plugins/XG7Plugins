package com.xg7plugins.modules.xg7npcs.npc.impl;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.living.impl.LivingAverageMobNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

public class MobNPC extends NPC {

    private final Consumer<NPCClickEvent> npcClickEventConsumer;

    public MobNPC(Plugin plugin, String id, Hologram hologram, EntityType entityType, Location spawnLocation, Map<EquipmentSlot, Item> equipments, boolean lookAtPlayer, Consumer<NPCClickEvent> npcClickEvent) {
        super(plugin, id, hologram, entityType, spawnLocation, equipments, lookAtPlayer);
        this.npcClickEventConsumer = npcClickEvent;
    }

    @Override
    public LivingNPC spawn(Player player) {
        LivingAverageMobNPC mobNPC = new LivingAverageMobNPC(player, this);
        mobNPC.spawn();
        XG7Plugins.getAPI().npcs().registerLivingNPC(mobNPC);
        return mobNPC;
    }

    @Override
    public void onClick(NPCClickEvent event) {
        if (npcClickEventConsumer != null) {
            npcClickEventConsumer.accept(event);
        }
    }
}
