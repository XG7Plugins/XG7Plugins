package com.xg7plugins.utils.text.newComponent.serializer.tags.events;

import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.events.HoverEvent;
import com.xg7plugins.utils.text.newComponent.events.action.HoverAction;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TagType;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TextTag;

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
    public void resolve(Component component, List<String> openArgs, List<String> closeArgs) {
        if (openArgs.size() < 2) {
            throw new IllegalArgumentException("Click tag must have 2 arguments at least");
        }

        HoverAction action = HoverAction.valueOf(openArgs.get(0).toUpperCase());

        String hoverContent = String.join(":", openArgs.subList(1, openArgs.size()));

        component.setHoverEvent(HoverEvent.of(action, hoverContent));
    }
}
