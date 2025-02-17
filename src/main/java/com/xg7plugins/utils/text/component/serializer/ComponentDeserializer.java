package com.xg7plugins.utils.text.component.serializer;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.tags.ClickTag;
import com.xg7plugins.utils.text.component.serializer.tags.GradientTag;
import com.xg7plugins.utils.text.component.serializer.tags.HexTag;
import com.xg7plugins.utils.text.component.serializer.tags.HoverTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentDeserializer {

    private static final Pattern TAG_PATTERN = Pattern.compile("<(\\w+)(?::([^>]*))?>(.*?)</\\1(?::([^>]*))?>", Pattern.CASE_INSENSITIVE);

    private static final HashMap<String, Tag> tags = new HashMap<>();

    static {
        tags.put("click", new ClickTag());
        tags.put("hex", new HexTag());
        tags.put("gradient", new GradientTag());
        tags.put("hover", new HoverTag());
    }

    public static Component deserialize(String text) {
        Matcher matcher = TAG_PATTERN.matcher(text);

        if (!matcher.find()) {
            return Component.text(text).build();
        }

        Component component = Component.EMPTY.clone();

        int lastIndex = 0;
        matcher.reset();

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                component.addComponent(Component.text(text.substring(lastIndex, matcher.start())).build());
            }

            String tagName = matcher.group(1);
            String[] openArgs = matcher.group(2) != null ? matcher.group(2).split(":") : new String[0];
            String content = matcher.group(3);
            String[] closeArgs = matcher.group(4) != null ? matcher.group(4).split(":") : new String[0];

            Component innerComponent = deserialize(content);

            Tag tag = tags.get(tagName);
            if (tag == null) throw new IllegalArgumentException("Unknown tag: " + tagName);

            Component resolveComponent = Component.EMPTY.clone();

            resolveComponent.setEvents(innerComponent.getEvents());
            resolveComponent.setText(innerComponent.getText());

            tag.resolve(resolveComponent, Arrays.asList(openArgs), Arrays.asList(closeArgs));

            lastIndex = matcher.end();

            component.addComponent(resolveComponent);
        }

        if (lastIndex < text.length()) component.addComponent(Component.text(text.substring(lastIndex)).build());

        return component;
    }

}
