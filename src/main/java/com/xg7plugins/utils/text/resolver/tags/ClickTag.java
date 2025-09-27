package com.xg7plugins.utils.text.resolver.tags;

import com.xg7plugins.utils.text.resolver.Tag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class ClickTag implements Tag {
    @Override
    public String name() {
        return "click";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs) {

        if (openArgs.size() < 2) {
            throw new IllegalArgumentException("Click tag must have 2 arguments at least");
        }

        ClickEvent.Action action = ClickEvent.Action.valueOf(openArgs.get(0).toUpperCase());

        String clickContent = String.join(":", openArgs.subList(1, openArgs.size()));

        component.setClickEvent(new ClickEvent(action, clickContent));
    }
}
