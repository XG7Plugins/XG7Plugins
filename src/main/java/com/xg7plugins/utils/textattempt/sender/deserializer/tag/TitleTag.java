package com.xg7plugins.utils.textattempt.sender.deserializer.tag;

import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.textattempt.sender.TextSender;
import com.xg7plugins.utils.textattempt.sender.TitleSender;

import java.util.List;

public class TitleTag implements SenderTag {
    @Override
    public TextSender resolve(List<String> args) {

        int fade = 3 * 20;
        int fadeIn = 20;
        int fadeOut = 20;

        switch (args.size()) {
            case 0:
                break;
            case 1:
                fade = Parser.INTEGER.convert(args.get(0));
                break;
            case 2:
                fadeIn = Parser.INTEGER.convert(args.get(0));
                fadeOut = Parser.INTEGER.convert(args.get(1));
                break;
            default:
                fadeIn = Parser.INTEGER.convert(args.get(0));
                fade = Parser.INTEGER.convert(args.get(1));
                fadeOut = Parser.INTEGER.convert(args.get(2));
                break;
        }

        return new TitleSender(fade, fadeIn, fadeOut);
    }

    @Override
    public String getName() {
        return "title";
    }
}
