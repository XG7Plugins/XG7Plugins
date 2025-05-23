package com.xg7plugins.utils.text.component.serializer;

import com.xg7plugins.utils.text.component.events.ChatEvent;
import com.xg7plugins.utils.text.component.modfiers.TextModifier;

public class TagSerializer {

    private char start;
    private char end;
    private final String content;
    private final ChatEvent chatEvent;
    private final TextModifier modifier;


    private TagSerializer(String content, ChatEvent chatEvent, TextModifier modifier) {
        if (chatEvent == null && modifier == null) throw new IllegalArgumentException("ChatEvent and Modifier cannot be null at the same time");

        this.content = content;
        this.chatEvent = chatEvent;
        this.modifier = modifier;

        if (chatEvent == null) {
            this.start = '(';
            this.end = ')';
            return;
        }
        if (modifier == null) {
            this.start = '<';
            this.end = '>';
        }
    }
    public TagSerializer(String content, ChatEvent chatEvent) {
        this(content, chatEvent, null);
    }
    public TagSerializer(String content, TextModifier modifier) {
        this(content, null, modifier);
    }

    public String serialize() {

        StringBuilder builder = new StringBuilder();

        String interTag = modifier == null ? chatEvent.serialize() : modifier.serialize();

        builder.append(start)
                .append(interTag)
                .append(end)
                .append(content)
                .append(start)
                .append("/")
                .append(interTag.split(":")[0])
                .append(end);

        return builder.toString();

    }


}
