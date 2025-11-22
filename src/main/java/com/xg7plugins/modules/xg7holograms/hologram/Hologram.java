package com.xg7plugins.modules.xg7holograms.hologram;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.location.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class Hologram {

    private final Plugin plugin;
    private final String id;
    private final List<HologramLine> lines;
    private Location location;

    private Consumer<HologramClickEvent> hologramClickEvent;

    public HologramLine getLine(int index) {
        return lines.get(index);
    }

    public void kill(Player player) {
        XG7Plugins.getAPI().holograms().unregisterLivingHologram(player.getUniqueId(), this.id);
    }

    public LivingHologram spawn(Player player) {
        LivingHologram livingHologram = new LivingHologram(player, this);
        livingHologram.spawn();
        XG7Plugins.getAPI().holograms().registerLivingHologram(livingHologram);
        return livingHologram;
    }

    public void onClick(HologramClickEvent event) {
        if (hologramClickEvent != null) {
            hologramClickEvent.accept(event);
        }
    };

}
