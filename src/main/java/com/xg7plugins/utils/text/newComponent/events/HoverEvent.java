package com.xg7plugins.utils.text.newComponent.events;

import com.xg7plugins.utils.text.newComponent.events.action.HoverAction;
import net.md_5.bungee.api.chat.TextComponent;

public class HoverEvent implements ChatEvent {
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

    public static HoverEvent of(HoverAction action, String content) {
        return new HoverEvent(content,action);
    }
}
