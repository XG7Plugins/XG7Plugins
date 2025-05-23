package com.xg7plugins.utils.text.sender.deserializer;

import com.xg7plugins.utils.text.sender.TextSender;
import com.xg7plugins.utils.text.sender.deserializer.tag.SenderTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class SenderTagMatch {

    private SenderTag tag;
    private final String name;
    private final String args;

    SenderTagMatch(Matcher matcher) {
        this.name = matcher.group(1).toLowerCase();
        this.args = matcher.groupCount() == 1 ? null : matcher.group(2);


        if (!TextSenderDeserializer.containsTag(name)) throw new IllegalArgumentException(String.format("Unknown tag '%s'", name));

        this.tag = TextSenderDeserializer.getTag(name);
    }

    public List<String> getArgs() {
        if (args == null) return new ArrayList<>();
        return Arrays.asList(args.split(":"));
    }

    public TextSender resolve() {
        return tag.resolve(getArgs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args, tag);
    }
}
