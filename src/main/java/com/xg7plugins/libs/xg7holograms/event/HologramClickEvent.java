package com.xg7plugins.libs.xg7holograms.event;

import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.libs.xg7holograms.holograms.HologramState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class HologramClickEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ClickType clickType;
    private final HologramState hologramState;

    public HologramClickEvent(@NotNull Player who, @NotNull ClickType clickType, @NotNull HologramState hologramState) {
        super(who);
        this.clickType = clickType;
        this.hologramState = hologramState;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
