package com.xg7plugins.libs.xg7holograms.holograms;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@Getter
public class HologramState {

    private final Hologram hologram;
    private final List<WrapperPlayServerSpawnEntity> hologramEntities;
    private final Player player;

    public void update() {
        hologram.update(this);
    }
    public void destroy() {
        hologram.destroy(this);
    }
}
