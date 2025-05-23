package com.xg7plugins.utils.text.sender.deserializer.tag;

import com.xg7plugins.utils.text.sender.TextSender;

import java.util.List;

public interface SenderTag {

    TextSender resolve(List<String> args);
    String getName();

}
