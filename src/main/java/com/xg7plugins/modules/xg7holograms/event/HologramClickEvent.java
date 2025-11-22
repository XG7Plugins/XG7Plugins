package com.xg7plugins.modules.xg7holograms.event;

import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class HologramClickEvent {

    private final Player player;
    private final LivingHologram livingHologram;
    private final ClickAction action;

}
