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

    public static HoverEvent of(Action action, String text) {
        return new HoverEvent(new net.md_5.bungee.api.chat.HoverEvent(action.toBungee(), Text.format(text).getComponent()));
    }

    public enum Action {
        SHOW_TEXT,
        SHOW_ITEM,
        SHOW_ENTITY,
        /** @deprecated */
        @Deprecated
        SHOW_ACHIEVEMENT;

        public net.md_5.bungee.api.chat.HoverEvent.Action toBungee() {
            return net.md_5.bungee.api.chat.HoverEvent.Action.valueOf(name());
        }
    }

}
