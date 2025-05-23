package com.xg7plugins.utils.text.component.events.action;

import net.md_5.bungee.api.chat.ClickEvent;

public enum ClickAction implements Action {

    OPEN_URL,
    OPEN_FILE,
    RUN_COMMAND,
    SUGGEST_COMMAND,
    CHANGE_PAGE,
    COPY_TO_CLIPBOARD;

    @Override
    public <T extends Enum<T>> T toBukkitAction() {
        return (T) ClickEvent.Action.valueOf(name());
    }
}
