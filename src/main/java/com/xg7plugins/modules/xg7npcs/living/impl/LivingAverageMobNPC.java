package com.xg7plugins.modules.xg7npcs.living.impl;

import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.utils.location.Location;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class LivingAverageMobNPC implements LivingNPC {

    private final Player player;
    private final NPC npc;

    private LivingHologram spawnedHologram = null;

    private Location currentLocation = null;

    private boolean moving = false;

    private int spawnedEntityID = -1;

    @Override
    public NPC getNPC() {
        return npc;
    }
}
