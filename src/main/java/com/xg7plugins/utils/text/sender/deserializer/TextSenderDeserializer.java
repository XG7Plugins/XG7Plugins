package com.xg7plugins.utils.text.sender.deserializer;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.sender.TextSender;
import com.xg7plugins.utils.text.sender.deserializer.tag.ActionTag;
import com.xg7plugins.utils.text.sender.deserializer.tag.CenterTag;
import com.xg7plugins.utils.text.sender.deserializer.tag.SenderTag;
import com.xg7plugins.utils.text.sender.deserializer.tag.TitleTag;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSenderDeserializer {

    private static final HashMap<String, SenderTag> senderTags = new HashMap<>();
    private static final Pattern SENDER_TAG_PATTERN = Pattern.compile("\\[([A-Za-z0-9]+)(?::(.*?))?] ");

    static {
        registerTag(new CenterTag());
        registerTag(new ActionTag());
        registerTag(new TitleTag());
    }

    public static void registerTag(SenderTag sender) {
        senderTags.put(sender.getName(), sender);
    }

    public static SenderTag getTag(String name) {
        return senderTags.get(name);
    }

    public static boolean containsTag(String name) {
        return senderTags.containsKey(name);
    }

    public static Pair<TextSender, String> extractSender(String text) {
        Matcher senderMatcher = SENDER_TAG_PATTERN.matcher(text);
        if (!senderMatcher.find()) return Pair.of(TextSender.defaultSender(), text);

        SenderTagMatch tagMatch = new SenderTagMatch(senderMatcher);

        TextSender textSender = tagMatch.resolve();

        return Pair.of(textSender, text.replaceFirst(SENDER_TAG_PATTERN.pattern(), "")) ;
    }





}
