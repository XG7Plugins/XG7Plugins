package com.xg7plugins.utils.textattempt.sender.deserializer.tag;

import com.xg7plugins.utils.textattempt.sender.ActionBarSender;
import com.xg7plugins.utils.textattempt.sender.TextSender;

import java.util.List;

public class ActionTag implements SenderTag {
    @Override
    public String getName() {
        return "action";
    }

    @Override
    public TextSender resolve(List<String> args) {
        return new ActionBarSender();
    }
}
