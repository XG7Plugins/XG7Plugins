package com.xg7plugins.utils.text.component;

import com.xg7plugins.utils.text.component.events.ClickEvent;
import com.xg7plugins.utils.text.component.events.HoverEvent;
import com.xg7plugins.utils.text.component.modfiers.TextModifier;
import com.xg7plugins.utils.text.component.sender.TextSender;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TextComponent {

    private List<Component> components = new ArrayList<>();
    private TextSender sender = TextSender.defaultSender();

    public TextComponent(List<Component> components) {
        components.forEach(this::addComponent);
    }
    public TextComponent(Component component) {
        addComponent(component);
    }
    public TextComponent(String text) {
        addComponent(new Component(text));
    }
    public TextComponent() {}

    public void addComponent(Component component) {
        component.setTextComponent(this);
        components.add(component);
    }
    public void addFirstComponent(Component component) {
        component.setTextComponent(this);
        components.add(0, component);
    }

    public BaseComponent[] toBukkitComponent() {
        ComponentBuilder builder = new ComponentBuilder("");

        String lastColors = ChatColor.RESET.toString();

        for (Component component : components) {
            String content = component.getContent();

            String currentColors = ChatColor.getLastColors(content);

            if (currentColors.isEmpty()) {
                builder.append(lastColors + content);
            } else {
                builder.append(content);
                lastColors = currentColors;
            }

            builder.event((net.md_5.bungee.api.chat.ClickEvent) null);
            builder.event((net.md_5.bungee.api.chat.HoverEvent) null);

            if (component.getModifiers() != null && !component.getModifiers().isEmpty()) component.getModifiers().forEach(modifier -> modifier.apply(component));

            if (component.getEvents() != null) {
                if (component.getEvents().getFirst() != null)
                    builder.event((net.md_5.bungee.api.chat.ClickEvent) component.getEvents().getFirst().toBukkitEvent());
                if (component.getEvents().getSecond() != null)
                    builder.event((net.md_5.bungee.api.chat.HoverEvent) component.getEvents().getSecond().toBukkitEvent());
            }
        }

        return builder.create();
    }


    public String getText() {
        return components.stream().map(Component::getContent).collect(Collectors.joining(" "));
    }

    public static class Builder {

        private final List<Component> components = new ArrayList<>();

        private Component currentComponent;

        public Builder(String text) {
            this.currentComponent = new Component(text);
        }

        public Builder text(String text) {
            currentComponent.setContent(text);
            return this;
        }

        public Builder onClick(ClickEvent event) {
            currentComponent.setClickEvent(event);
            return this;
        }

        public Builder onHover(HoverEvent event) {
            currentComponent.setHoverEvent(event);
            return this;
        }

        public Builder modifier(TextModifier modifier) {
            currentComponent.addModifier(modifier);
            return this;
        }

        public Builder append(String text) {
            components.add(currentComponent);
            this.currentComponent = new Component(text);
            return this;
        }

        public TextComponent build() {
            components.add(currentComponent);
            return new TextComponent(components);
        }

    }

    public void send(CommandSender sender) {
        this.sender.send(sender, this);
    }

    @Override
    public String toString() {

        List<String> components = this.components.stream().map(c -> {
            Component newC = c.clone();
            newC.setTextComponent(null);
            return newC.toString();
        }).collect(Collectors.toList());

        return "TextComponent{" +
                "components=" + components +
                ", sender=" + sender +
                '}';
    }

    public static TextComponent empty() {
        return new TextComponent(Component.EMPTY.clone());
    }
}
