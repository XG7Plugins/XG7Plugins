package com.xg7plugins.modules.xg7npcs.npc;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Map;

@Data
public abstract class NPC {

    private final Plugin plugin;
    private final String id;

    private final Hologram hologram;
    private final EntityType entityType;
    private final Location spawnLocation;

    private final Map<EquipmentSlot, Item> equipments;
    private final boolean lookAtPlayer;
    private final boolean glow;

    public abstract LivingNPC spawn(Player player);
    public abstract void onClick(NPCClickEvent event);




}
