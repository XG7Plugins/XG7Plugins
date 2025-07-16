package com.xg7plugins.utils.text.resolver.tags;


import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.resolver.Tag;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class HoverTag implements Tag {
    @Override
    public String name() {
        return "hover";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs, List<String> closeArgs) {

        if (openArgs.size() < 2) {
            throw new IllegalArgumentException("Click tag must have 2 arguments at least");
        }

        HoverEvent.Action action = HoverEvent.Action.valueOf(openArgs.get(0).toUpperCase());

        String hoverContent = String.join(":", openArgs.subList(1, openArgs.size()));

        component.setHoverEvent(new HoverEvent(action, Text.format(hoverContent).getComponent()));
    }
}
