package com.xg7plugins.utils.text.component.event;

import com.xg7plugins.utils.text.component.event.action.HoverAction;
import net.md_5.bungee.api.chat.TextComponent;

public class HoverEvent implements Event {
    private final String content;
    private final HoverAction action;

    public HoverEvent(String content, HoverAction action) {
        this.content = content;
        this.action = action;
    }

    public String content() {
        return content;
    }

    public HoverAction action() {
        return action;
    }

    @Override
    public <T> T toBukkitEvent() {
        return (T) new net.md_5.bungee.api.chat.HoverEvent(action.toBukkitAction(), TextComponent.fromLegacyText(content));
    }
}
