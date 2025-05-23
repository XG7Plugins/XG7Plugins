package com.xg7plugins.utils.text;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.events.ClickEvent;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.modfiers.TextModifier;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    private String content;
    private final List<TextModifier> modifiers = new ArrayList<>();
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private TextComponent textComponent;

    public ComponentBuilder(String content) {
        this.content = content;
    }

    public ComponentBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ComponentBuilder addModifier(TextModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public ComponentBuilder modifiers(List<TextModifier> modifiers) {
        this.modifiers.addAll(modifiers);
        return this;
    }

    public ComponentBuilder clickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public ComponentBuilder hoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    public ComponentBuilder textComponent(TextComponent textComponent) {
        this.textComponent = textComponent;
        return this;
    }

    public Component build() {
        Pair<ClickEvent, HoverEvent> events = null;

        if (clickEvent != null || hoverEvent != null) events = new Pair<>(clickEvent, hoverEvent);

        return new Component(
                content != null ? content : "",
                new ArrayList<>(modifiers),
                events,
                textComponent
        );
    }

    public TextComponent buildTextComponent() {
        return new TextComponent(build());
    }

    public static ComponentBuilder builder(String content) {
        return new ComponentBuilder(content);
    }
}
