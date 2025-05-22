package com.xg7plugins.utils.text.newComponent;

import com.xg7plugins.utils.text.newComponent.events.ClickEvent;
import com.xg7plugins.utils.text.newComponent.events.HoverEvent;
import com.xg7plugins.utils.text.newComponent.modfiers.TextModifier;
import com.xg7plugins.utils.text.newComponent.sender.TextSender;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TextComponent {

    private List<Component> components = new ArrayList<>();
    private TextSender sender = TextSender.defaultSender();

    public TextComponent(List<Component> components) {
        this.components = components;
    }
    public TextComponent(Component component) {
        this.components.add(component);
    }
    public TextComponent(String text) {
        this.components.add(new Component(text));
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

        for (Component component : components) {
            builder.append(component.toBukkitComponent());
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
}
