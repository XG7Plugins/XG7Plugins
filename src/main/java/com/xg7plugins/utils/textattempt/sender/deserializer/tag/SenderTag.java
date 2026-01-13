package com.xg7plugins.utils.textattempt.sender.deserializer.tag;

import com.xg7plugins.utils.textattempt.sender.TextSender;

import java.util.List;

public interface SenderTag {

    TextSender resolve(List<String> args);
    String getName();

}
