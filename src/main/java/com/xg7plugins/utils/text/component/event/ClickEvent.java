package com.xg7plugins.utils.text.component.event;

import com.xg7plugins.utils.text.component.event.action.ClickAction;

public class ClickEvent implements Event {

    private final String content;
    private final ClickAction action;

    public ClickEvent(String content, ClickAction action) {
        this.content = content;
        this.action = action;
    }

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


}
