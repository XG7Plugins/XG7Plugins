package com.xg7plugins.utils.text.component;

import com.xg7plugins.utils.text.Text;

public class ClickEvent {

    private net.md_5.bungee.api.chat.ClickEvent bungeeEvent;

    public ClickEvent(net.md_5.bungee.api.chat.ClickEvent bungeeEvent) {
        this.bungeeEvent = bungeeEvent;
    }

    public net.md_5.bungee.api.chat.ClickEvent toBungee() {
        return bungeeEvent;
    }

    public static ClickEvent of(Action action, String text) {
        return new ClickEvent(new net.md_5.bungee.api.chat.ClickEvent(action.toBungee(), text));
    }

    public enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE,
        COPY_TO_CLIPBOARD;

        public net.md_5.bungee.api.chat.ClickEvent.Action toBungee() {
            return net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(name());
        }
    }

}
