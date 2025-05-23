package com.xg7plugins.utils.text.component.deserializer.tags.senders;

import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.sender.CenterSender;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;

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
    public void resolve(Component component, List<String> args) {

        if (args.size() != 1) {
            throw new IllegalArgumentException("Center tag must have 1 arguments");
        }

        int size = 0;

        try {
            TextCentralizer.PixelsSize pixelsSize = TextCentralizer.PixelsSize.valueOf(args.get(0).toUpperCase());

            size = pixelsSize.getPixels();
        } catch (IllegalArgumentException ignored) {
            size = Integer.parseInt(args.get(0));
        }

        component.getTextComponent().setSender(new CenterSender(size));
    }
}
