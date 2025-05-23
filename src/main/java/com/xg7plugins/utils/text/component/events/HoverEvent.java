package com.xg7plugins.utils.text.component.events;

import com.xg7plugins.utils.text.component.events.action.HoverAction;
import lombok.AllArgsConstructor;
import lombok.ToString;
import net.md_5.bungee.api.chat.TextComponent;

@ToString
@AllArgsConstructor
public class HoverEvent implements ChatEvent {
    private final String content;
    private final HoverAction action;

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

    @Override
    public String serialize() {
        return "hover:" + action.name() + ":" + content;
    }

}
