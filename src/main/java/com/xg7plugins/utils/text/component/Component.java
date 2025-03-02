package com.xg7plugins.utils.text.component;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.component.event.ClickEvent;
import com.xg7plugins.utils.text.component.event.HoverEvent;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

@Data
public class Component implements Cloneable {
    public static final Component EMPTY = new Component("", null, null);

    private String text;
    private Pair<HoverEvent, ClickEvent> events;
    private List<Component> components;

    public Component(String text, Pair<HoverEvent, ClickEvent> events, List<Component> components) {
        this.text = text;
        this.events = events;
        this.components = components;
    }

    public String content() {
        StringBuilder builder = new StringBuilder(text);

        if (components == null || components.isEmpty()) return builder.toString();
        for (Component component : components) {
            builder.append(component.content());
        }

        return builder.toString();
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void addFirstComponent(Component component) {
        components.add(0, component);
    }

    public void setClickEvent(ClickEvent event) {
        if (this.events == null) this.events = new Pair<>(null, null);

        events.setSecond(event);
    }

    public void setHoverEvent(HoverEvent event) {
        if (this.events == null) this.events = new Pair<>(null, null);

        events.setFirst(event);
    }

    public boolean isEmpty() {
        return text.isEmpty() && (components == null || components.isEmpty());
    }

    public BaseComponent[] toBukkitComponent() {
        ComponentBuilder builder = new ComponentBuilder(text);
        if (events != null) {
            if (events.getFirst() != null) {
                builder.event((net.md_5.bungee.api.chat.HoverEvent) events.getFirst().toBukkitEvent());
            }
            if (events.getSecond() != null) {
                builder.event((net.md_5.bungee.api.chat.ClickEvent) events.getSecond().toBukkitEvent());
            }
        }

        if (components != null) {

            List<Component> toProcess = new ArrayList<>();
            Stack<Component> stack = new Stack<>();

            for (int i = components.size() - 1; i >= 0; i--) stack.push(components.get(i));


            while (!stack.isEmpty()) {
                Component currentComponent = stack.pop();
                toProcess.add(currentComponent);

                List<Component> subComponents = currentComponent.getComponents();
                for (int i = subComponents.size() - 1; i >= 0; i--) stack.push(subComponents.get(i));
            }

            for (Component component : toProcess) {
                builder.append(component.getText());
                if (component.getEvents() != null) {
                    if (component.getEvents().getFirst() != null) {
                        builder.event((net.md_5.bungee.api.chat.HoverEvent) component.getEvents().getFirst().toBukkitEvent());
                    }
                    if (component.getEvents().getSecond() != null) {
                        builder.event((net.md_5.bungee.api.chat.ClickEvent) component.getEvents().getSecond().toBukkitEvent());
                    }
                }
            }
        }

        return builder.create();
    }

    @Override
    public Component clone() {
        try {
            return (Component) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static Builder text() {
        return new Builder("");
    }
    public static Builder text(String text) {
        return new Builder(text);
    }

    public static Builder component(Component component) {
        return new Builder(component);
    }

    public static class Builder {

        private final Component component;

        private String currentText;
        private final Pair<HoverEvent, ClickEvent> events = new Pair<>(null, null);

        public Builder(String text) {
            this.currentText = text;
            this.component = Component.EMPTY.clone();
            component.setEvents(new Pair<>(null,null));
            component.setComponents(new ArrayList<>());
        }
        public Builder(Component component) {
            this.currentText = component.content();
            this.component = component;
            if (component.getComponents() == null) {
                component.setComponents(new ArrayList<>());
            }
        }

        public Builder text(String text) {
            this.currentText = text;
            return this;
        }

        public Builder onClick(ClickEvent event) {
            events.setSecond(event);
            return this;
        }

        public Builder onHover(HoverEvent event) {
            events.setFirst(event);
            return this;
        }

        public Builder append(String text) {
            component.addComponent(new Component(currentText, events, new ArrayList<>()));
            currentText = text;
            events.setFirst(null);
            events.setSecond(null);
            return this;
        }

        public Component build() {
            component.addComponent(new Component(currentText, events, new ArrayList<>()));
            return component;
        }


    }







}
