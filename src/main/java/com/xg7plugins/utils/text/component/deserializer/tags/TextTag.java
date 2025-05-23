package com.xg7plugins.utils.text.component.deserializer.tags;

import com.xg7plugins.utils.text.component.Component;

import java.util.List;

public interface TextTag {

    String name();
    TagType getType();

    void resolve(Component component, List<String> args);

}
