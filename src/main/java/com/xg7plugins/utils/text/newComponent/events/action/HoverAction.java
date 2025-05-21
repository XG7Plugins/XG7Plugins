package com.xg7plugins.utils.text.newComponent.events.action;

import net.md_5.bungee.api.chat.HoverEvent;

public enum HoverAction implements Action {

    SHOW_TEXT,
    SHOW_ITEM,
    SHOW_ENTITY;

    @Override
    public <T extends Enum<T>> T toBukkitAction() {
        return (T) HoverEvent.Action.valueOf(name());
    }
}
