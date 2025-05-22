package com.xg7plugins.utils.text.newComponent;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.newComponent.events.ClickEvent;
import com.xg7plugins.utils.text.newComponent.events.HoverEvent;
import com.xg7plugins.utils.text.newComponent.modfiers.TextModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Component implements Cloneable {

    public static final Component EMPTY = new Component("");

    private String content;
    private List<TextModifier> modifiers;
    private Pair<ClickEvent, HoverEvent> events;
    private TextComponent textComponent;

    public Component(String content) {
        this(content, new ArrayList<>(), new Pair<>(null, null), null);
    }

    public void setClickEvent(ClickEvent event) {
        if (this.events == null) this.events = new Pair<>(null, null);
        this.events.setFirst(event);
    }

    public void setHoverEvent(HoverEvent event) {
        if (this.events == null) this.events = new Pair<>(null, null);
        this.events.setSecond(event);
    }

    public void addModifier(TextModifier modifier) {
        if (this.modifiers == null) this.modifiers = new ArrayList<>();
        this.modifiers.add(modifier);
    }

    public BaseComponent[] toBukkitComponent() {

        if (modifiers != null && !modifiers.isEmpty()) modifiers.forEach(modifier -> modifier.apply(this));

        ComponentBuilder component = new ComponentBuilder(content);

        if (events != null) {
            if (events.getFirst() != null) component.event((net.md_5.bungee.api.chat.ClickEvent) events.getFirst().toBukkitEvent());
            if (events.getSecond() != null) component.event((net.md_5.bungee.api.chat.ClickEvent) events.getSecond().toBukkitEvent());
        }

        return component.create();
    }

    @Override
    public Component clone() {
        try {
            return (Component) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        if (textComponent != null) return textComponent.toString();
        return "Component{" +
                "content='" + content + '\'' +
                ", modifiers=" + modifiers +
                ", events=" + events +
                ", textComponent=" + textComponent +
                '}';
    }
}
