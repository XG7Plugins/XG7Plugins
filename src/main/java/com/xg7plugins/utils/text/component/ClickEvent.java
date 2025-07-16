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

    public static ClickEvent of(net.md_5.bungee.api.chat.ClickEvent.Action action, String text) {
        return new ClickEvent(new net.md_5.bungee.api.chat.ClickEvent(action, text));
    }

}
