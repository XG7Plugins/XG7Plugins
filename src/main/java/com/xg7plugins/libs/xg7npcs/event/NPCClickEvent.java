package com.xg7plugins.libs.xg7npcs.event;

import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCClickEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ClickType clickType;
    private final NPC npc;

    public NPCClickEvent(@NotNull Player who, @NotNull ClickType clickType, @NotNull NPC npc) {
        super(who);
        this.clickType = clickType;
        this.npc = npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
