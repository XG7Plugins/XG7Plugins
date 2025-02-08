package com.xg7plugins.temp.xg7holograms.event;

import com.xg7plugins.temp.xg7holograms.holograms.Hologram;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class HologramClickEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ClickType clickType;
    private final Hologram hologram;

    public HologramClickEvent(@NotNull Player who, @NotNull ClickType clickType, @NotNull Hologram hologram) {
        super(who);
        this.clickType = clickType;
        this.hologram = hologram;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
