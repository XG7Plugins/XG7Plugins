package com.xg7plugins.utils.text.component.events;

import com.xg7plugins.utils.text.component.events.action.ClickAction;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ClickEvent implements ChatEvent {

    private final String content;
    private final ClickAction action;

    public String content() {
        return content;
    }

    public ClickAction action() {
        return action;
    }

    @Override
    public <T> T toBukkitEvent() {
        return (T) new net.md_5.bungee.api.chat.ClickEvent(action.toBukkitAction(), content);
    }

    public static ClickEvent of(ClickAction action, String content) {
        return new ClickEvent(content,action);
    }

    @Override
    public String serialize() {
        return "click:" + action.name() + ":" + content;
    }

}
