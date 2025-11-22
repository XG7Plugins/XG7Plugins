package com.xg7plugins.modules.xg7npcs.event;

import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class NPCClickEvent {

    private final Player player;
    private final LivingNPC livingNPC;
    private final ClickAction clickAction;

}
