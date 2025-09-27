package com.xg7plugins.utils.text.resolver;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public interface Tag {

    String name();

    void resolve(TextComponent component, List<String> openArgs);

}
