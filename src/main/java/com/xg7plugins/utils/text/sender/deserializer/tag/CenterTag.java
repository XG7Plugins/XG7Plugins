package com.xg7plugins.utils.text.sender.deserializer.tag;

import com.xg7plugins.utils.text.TextCentralizer;
import com.xg7plugins.utils.text.sender.CenterSender;
import com.xg7plugins.utils.text.sender.TextSender;

import java.util.List;

public class CenterTag implements SenderTag {
    @Override
    public TextSender resolve(List<String> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Center tag must have 1 arguments");
        }

        int size;

        try {
            TextCentralizer.PixelsSize pixelsSize = TextCentralizer.PixelsSize.valueOf(args.get(0).toUpperCase());

            size = pixelsSize.getPixels();
        } catch (IllegalArgumentException ignored) {
            size = Integer.parseInt(args.get(0));
        }

        return new CenterSender(size);
    }

    @Override
    public String getName() {
        return "center";
    }
}
