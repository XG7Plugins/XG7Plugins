package com.xg7plugins.utils.text.component.deserializer.tags.events;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.events.action.HoverAction;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;

import java.util.List;

public class HoverTag implements TextTag {
    @Override
    public String name() {
        return "hover";
    }

    @Override
    public TagType getType() {
        return TagType.EVENT;
    }

    @Override
    public void resolve(Component component, List<String> args) {
        if (args.size() < 2) {
            throw new IllegalArgumentException("Click tag must have 2 arguments at least");
        }

        HoverAction action = HoverAction.valueOf(args.get(0).toUpperCase());

        String hoverContent = String.join(":", args.subList(1, args.size()));

        component.setHoverEvent(HoverEvent.of(action, hoverContent));
    }
}
