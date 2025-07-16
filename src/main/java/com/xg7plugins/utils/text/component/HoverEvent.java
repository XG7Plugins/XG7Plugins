package com.xg7plugins.utils.text.component;

import com.xg7plugins.utils.text.Text;

public class HoverEvent {

    private net.md_5.bungee.api.chat.HoverEvent bungeeEvent;

    public HoverEvent(net.md_5.bungee.api.chat.HoverEvent bungeeEvent) {
        this.bungeeEvent = bungeeEvent;
    }

    public net.md_5.bungee.api.chat.HoverEvent toBungee() {
        return bungeeEvent;
    }

    public static HoverEvent of(net.md_5.bungee.api.chat.HoverEvent.Action action, String text) {
        return new HoverEvent(new net.md_5.bungee.api.chat.HoverEvent(action, Text.format(text).getComponent()));
    }

}
