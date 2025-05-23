package com.xg7plugins.utils.text.component.serializer;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.events.ClickEvent;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.modfiers.TextModifier;

public class ComponentSerializer {


    public static String serialize(TextComponent component) {

        StringBuilder builder = new StringBuilder();

        if (component.getSender() != null && !component.getSender().serialize().equalsIgnoreCase("default")) builder.append("[").append(component.getSender().serialize()).append("] ");


        component.getComponents().forEach(component1 -> builder.append(serialize(component1)));

        return builder.toString();
    }

    public static String serialize(Component component) {
        String content = component.getContent();

        for (TextModifier modifier : component.getModifiers()) {
            TagSerializer serializer = new TagSerializer(content, modifier);
            content = serializer.serialize();
        }

        if (component.getEvents() != null) {
            ClickEvent clickEvent = component.getEvents().getFirst();
            HoverEvent hoverEvent = component.getEvents().getSecond();

            if (hoverEvent != null) {
                TagSerializer serializer = new TagSerializer(content, hoverEvent);
                content = serializer.serialize();
            }
            if (clickEvent != null) {
                TagSerializer serializer = new TagSerializer(content, clickEvent);
                content = serializer.serialize();
            }
        }

        return content;
    }
}
