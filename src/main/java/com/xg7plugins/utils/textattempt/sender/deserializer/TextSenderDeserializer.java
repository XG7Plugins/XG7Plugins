package com.xg7plugins.utils.textattempt.sender.deserializer;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.textattempt.sender.TextSender;
import com.xg7plugins.utils.textattempt.sender.deserializer.tag.ActionTag;
import com.xg7plugins.utils.textattempt.sender.deserializer.tag.CenterTag;
import com.xg7plugins.utils.textattempt.sender.deserializer.tag.SenderTag;
import com.xg7plugins.utils.textattempt.sender.deserializer.tag.TitleTag;

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

        try {
            SenderTagMatch tagMatch = new SenderTagMatch(senderMatcher);

            TextSender textSender = tagMatch.resolve();

            return Pair.of(textSender, text.replaceFirst(SENDER_TAG_PATTERN.pattern(), "")) ;
        } catch (Exception e) {
            XG7Plugins.getInstance().getDebug().warn("text", "Failed to parse sender tag in text: " + text + ". Error: " + e.getMessage());
            return Pair.of(TextSender.defaultSender(), text);
        }
    }





}
