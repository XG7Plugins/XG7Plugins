package com.xg7plugins.utils.text.resolver;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

/**
 * Interface representing a tag that can be resolved in a text component.
 */
public interface Tag {

    /**
     * Get the name of the tag.
     *
     * @return the name of the tag
     */
    String name();

    /**
     * Resolve the tag in the given text component with the provided arguments.
     *
     * @param component the text component to resolve the tag in
     * @param openArgs  the list of arguments for the tag
     */
    void resolve(TextComponent component, List<String> openArgs);

}
