package com.xg7plugins.utils.text.component.deserializer.tags.senders;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.sender.ActionBarSender;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;

import java.util.List;

public class ActionTag implements TextTag {
    @Override
    public String name() {
        return "action";
    }

    @Override
    public TagType getType() {
        return TagType.SENDER;
    }

    @Override
    public void resolve(Component component, List<String> args) {
        component.getTextComponent().setSender(new ActionBarSender());
    }
}
