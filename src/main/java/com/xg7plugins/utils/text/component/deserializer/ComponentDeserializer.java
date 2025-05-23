package com.xg7plugins.utils.text.component.deserializer;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;
import com.xg7plugins.utils.text.component.deserializer.tags.events.ClickTag;
import com.xg7plugins.utils.text.component.deserializer.tags.events.HoverTag;
import com.xg7plugins.utils.text.component.deserializer.tags.modifiers.GradientTag;
import com.xg7plugins.utils.text.component.deserializer.tags.modifiers.HexTag;
import com.xg7plugins.utils.text.component.deserializer.tags.senders.ActionTag;
import com.xg7plugins.utils.text.component.deserializer.tags.senders.CenterTag;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentDeserializer {

    private static final Pattern EVENT_TAG_PATTERN = Pattern.compile("<([A-Za-z0-9]+)(?::([^>]+))?>(.*?)</\\1>");
    private static final Pattern MODIFIER_TAG_PATTERN = Pattern.compile("\\(([A-Za-z0-9]+)(?::([^)]+))?\\)(.*?)\\(/\\1\\)");
    private static final Pattern SENDER_TAG_PATTERN = Pattern.compile("\\[([A-Za-z0-9]+)(?::(.*?))?] ");

    private static final HashMap<String, TextTag> tags = new HashMap<>();

    static {
        putTag(new ActionTag());
        putTag(new CenterTag());
        putTag(new HoverTag());
        putTag(new ClickTag());
        putTag(new HexTag());
        putTag(new GradientTag());
    }

    public static TextComponent deserialize(String text) {
        TextComponent textComponent = new TextComponent(Component.EMPTY.clone());
        text = processSenderTag(text, textComponent);
        processText(text, textComponent);

        System.out.println(textComponent);

        return textComponent;
    }

    /**
     * Processa a tag SENDER se existir no início do texto
     */
    private static String processSenderTag(String text, TextComponent textComponent) {
        Matcher senderMatcher = SENDER_TAG_PATTERN.matcher(text);
        if (!senderMatcher.find()) return text;

        TagMatch tagMatch = new TagMatch(TagType.SENDER, senderMatcher);

        tagMatch.resolve(textComponent.getComponents().get(0));

        return text.replaceFirst(SENDER_TAG_PATTERN.pattern(), "");
    }

    /**
     * Processa texto com lógica inside-out: tags internas primeiro, depois externas
     */
    private static void processText(String text, TextComponent textComponent) {
        Matcher eventMatcher = EVENT_TAG_PATTERN.matcher(text);
        Matcher modifierMatcher = MODIFIER_TAG_PATTERN.matcher(text);

        List<String> parts = new ArrayList<>();
        int lastEnd = 0;

        boolean hasEvent = eventMatcher.find();
        boolean hasModfier = modifierMatcher.find();

        while (hasEvent || hasModfier) {
            int startEvent = hasEvent ? eventMatcher.start() : Integer.MAX_VALUE;
            int startModifier = hasModfier ? modifierMatcher.start() : Integer.MAX_VALUE;

            if (startEvent <= startModifier) {
                if (startEvent > lastEnd) parts.add(text.substring(lastEnd, startEvent));
                parts.add(eventMatcher.group());
                lastEnd = eventMatcher.end();
                hasEvent = eventMatcher.find();
                continue;
            }

            if (startModifier > lastEnd) parts.add(text.substring(lastEnd, startModifier));

            parts.add(modifierMatcher.group());
            lastEnd = modifierMatcher.end();
            hasModfier = modifierMatcher.find();

        }
        if (lastEnd < text.length()) parts.add(text.substring(lastEnd));

        for (String part : parts) {
            System.out.println("Deserialing: " + part);
            textComponent.addComponent(processPart(part));
        }
    }

    private static Component processPart(String part) {

        Component component = Component.EMPTY.clone();

        component.setContent(part);

        Matcher eventMatcher = EVENT_TAG_PATTERN.matcher(part);

        if (eventMatcher.find() && eventMatcher.start() == 0) {
            TagMatch tagMatch = new TagMatch(TagType.EVENT, eventMatcher);
            System.out.println("Content: " + eventMatcher.replaceAll("$3"));
            Component innerComponent = processPart(eventMatcher.replaceAll("$3"));
            component.merge(innerComponent);
            component.setContent(innerComponent.getContent());
            tagMatch.resolve(component);
        }

        Matcher modifierMatcher = MODIFIER_TAG_PATTERN.matcher(part);

        if (modifierMatcher.find() && modifierMatcher.start() == 0) {
            TagMatch tagMatch = new TagMatch(TagType.MODIFIER, modifierMatcher);
            Component innerComponent = processPart(modifierMatcher.replaceAll("$3"));
            component.merge(innerComponent);
            component.setContent(innerComponent.getContent());
            tagMatch.resolve(component);
        }
        System.out.println("Component final: " + component.toString());

        return component;

    }

    public static void putTag(TextTag tag) {
        tags.put(tag.name(), tag);
    }

    public static  <T extends TextTag> T getTag(String name) {
        return (T) tags.get(name);
    }

    public static boolean containsTag(String name) {
        return tags.containsKey(name);
    }

}
