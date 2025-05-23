package com.xg7plugins.utils.text.sender.deserializer.tag;

import com.xg7plugins.utils.text.sender.ActionBarSender;
import com.xg7plugins.utils.text.sender.TextSender;

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
