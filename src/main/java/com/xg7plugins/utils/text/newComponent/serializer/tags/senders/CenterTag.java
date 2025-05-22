package com.xg7plugins.utils.text.newComponent.serializer.tags.senders;

import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.sender.CenterSender;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TagType;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TextTag;

import java.util.List;

public class CenterTag implements TextTag {
    @Override
    public String name() {
        return "center";
    }

    @Override
    public TagType getType() {
        return TagType.SENDER;
    }

    @Override
    public void resolve(Component component, List<String> openArgs, List<String> closeArgs) {

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Gradient tag must have 1 open arguments");
        }

        int size = 0;

        try {
            TextCentralizer.PixelsSize pixelsSize = TextCentralizer.PixelsSize.valueOf(openArgs.get(0).toUpperCase());

            size = pixelsSize.getPixels();
        } catch (IllegalArgumentException ignored) {
            size = Integer.parseInt(openArgs.get(0));
        }

        component.getTextComponent().setSender(new CenterSender(size));
    }
}
