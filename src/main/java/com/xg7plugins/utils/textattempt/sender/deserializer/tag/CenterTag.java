package com.xg7plugins.utils.textattempt.sender.deserializer.tag;

import com.xg7plugins.utils.textattempt.Text;
import com.xg7plugins.utils.textattempt.sender.CenterSender;
import com.xg7plugins.utils.textattempt.sender.TextSender;

import java.util.List;

public class CenterTag implements SenderTag {
    @Override
    public TextSender resolve(List<String> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Center tag must have 1 arguments");
        }

        int size;

        try {
            Text.PixelsSize pixelsSize = Text.PixelsSize.valueOf(args.get(0).toUpperCase());

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
