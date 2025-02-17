package com.xg7plugins.utils.text.component.serializer;

import com.xg7plugins.utils.text.component.Component;

import java.util.List;

public interface Tag {

    String name();

    void resolve(Component content, List<String> openArgs, List<String> closeArgs);

}
