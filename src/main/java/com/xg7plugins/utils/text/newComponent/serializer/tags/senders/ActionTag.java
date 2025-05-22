package com.xg7plugins.utils.text.newComponent.serializer.tags.senders;

import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TagType;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TextTag;

import java.util.List;

public class ActionTag implements TextTag {
    @Override
    public String name() {
        return "";
    }

    @Override
    public TagType getType() {
        return null;
    }

    @Override
    public void resolve(Component component, List<String> openArgs, List<String> closeArgs) {
    }
}
