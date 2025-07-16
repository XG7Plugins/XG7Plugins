package com.xg7plugins.utils.text.component;

import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TextComponentBuilder {

    private final ComponentBuilder builder = new ComponentBuilder("");

    public TextComponentBuilder append(String text) {
        builder.append(text);
        return this;
    }

    public TextComponentBuilder clickEvent(ClickEvent event) {
        builder.event(event.toBungee());
        return this;
    }

    public TextComponentBuilder hoverEvent(HoverEvent event) {
        builder.event(event.toBungee());
        return this;
    }

    public BaseComponent[] buildComponent() {
        return builder.create();
    }

    public Text build() {
        return new Text(buildComponent());
    }

    public static TextComponentBuilder of(String text) {
        return new TextComponentBuilder().append(text);
    }
    public static TextComponentBuilder builder() {
        return new TextComponentBuilder();
    }




}
