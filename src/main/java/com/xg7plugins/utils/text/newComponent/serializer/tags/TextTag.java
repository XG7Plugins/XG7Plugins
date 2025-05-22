package com.xg7plugins.utils.text.newComponent.serializer.tags;

import com.xg7plugins.utils.text.newComponent.Component;

import java.util.List;

public interface TextTag {

    String name();
    TagType getType();

    void resolve(Component component, List<String> openArgs, List<String> closeArgs);

}
