package com.xg7plugins.utils.text.component.serializer.tags;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.HoverEvent;
import com.xg7plugins.utils.text.component.event.action.ClickAction;
import com.xg7plugins.utils.text.component.serializer.Tag;

import java.util.List;

public class ClickTag implements Tag {
    @Override
    public String name() {
        return "click";
    }

    @Override
    public void resolve(Component content, List<String> openArgs, List<String> closeArgs) {

        if (openArgs.size() < 2) {
            throw new IllegalArgumentException("Click tag must have 2 arguments at least");
        }

        ClickAction action = ClickAction.valueOf(openArgs.get(0).toUpperCase());

        String clickContent = String.join(":", openArgs.subList(1, openArgs.size()));

        Pair<HoverEvent, ClickEvent> pair = content.getEvents();
        if (pair == null) {
            pair = new Pair<>(null, null);
        }
        pair.setSecond(new ClickEvent(clickContent, action));

        content.setEvents(pair);


    }
}
