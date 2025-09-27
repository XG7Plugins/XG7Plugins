package com.xg7plugins.utils.text.resolver;

import com.xg7plugins.utils.text.component.TextComponentBuilder;
import com.xg7plugins.utils.text.resolver.tags.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagResolver {

    private static final Pattern TAG_PATTERN = Pattern.compile("<(\\w+)(?::([^>]*))?>(.*?)</\\1>");

    private static final HashMap<String, Tag> tags = new HashMap<>();

    static {
        tags.put("click", new ClickTag());
        tags.put("hex", new HexTag());
        tags.put("gradient", new GradientTag());
        tags.put("hover", new HoverTag());
        tags.put("rainbow", new RainbowTag());
    }

    public static BaseComponent[] deserialize(String text) {
        try {
            Matcher test = TAG_PATTERN.matcher(text);

            if (!test.find()) {
                return new ComponentBuilder(text).create();
            }

            List<BaseComponent> components = new ArrayList<>();
            int lastIndex = 0;
            Matcher matcher = TAG_PATTERN.matcher(text);

            String lastColors = "";

            while (matcher.find()) {

                if (matcher.start() > lastIndex) {
                    String startText = text.substring(lastIndex, matcher.start());
                    lastColors = ChatColor.getLastColors(startText);

                    TextComponent before = new TextComponent(startText);
                    components.add(before);
                }

                String tagName = matcher.group(1);
                String[] openArgs = matcher.group(2) != null ? matcher.group(2).split(":") : new String[0];
                String content = lastColors + matcher.group(3);

                TextComponent innerComponent = new TextComponent(deserialize(content));

                Tag tag = tags.get(tagName);
                if (tag == null) throw new IllegalArgumentException("Unknown tag: " + tagName);

                tag.resolve(innerComponent, Arrays.asList(openArgs));

                lastColors = getLastColorsOf(innerComponent.getExtra());

                lastIndex = matcher.end();
                components.add(innerComponent);
            }

            if (lastIndex < text.length()) {
                String remaining = text.substring(lastIndex);
                TextComponent remainingComponent = new TextComponent(lastColors + remaining);
                components.add(remainingComponent);
            }

            return components.toArray(new BaseComponent[0]);

        } catch (Exception e) {
            e.printStackTrace();
            return new ComponentBuilder(text).create();
        }
    }

    public static String getLastColorsOf(List<BaseComponent> components) {
        StringBuilder builder = new StringBuilder();

        for (BaseComponent c : components) {
            if (c instanceof TextComponent) {
                String legacy = ((TextComponent) c).toLegacyText();
                builder.append(ChatColor.getLastColors(legacy));
            }
        }

        return builder.toString();
    }

    public static String removeTags(String input) {
        Matcher matcher = TAG_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String innerContent = removeTags(matcher.group(3));
            matcher.appendReplacement(result, Matcher.quoteReplacement(innerContent));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static String serialize(BaseComponent... components) {
        StringBuilder builder = new StringBuilder();

        for (BaseComponent component : components) {
            builder.append(serializeComponent(component));
        }

        return builder.toString();
    }

    private static String serializeComponent(BaseComponent component) {
        StringBuilder result = new StringBuilder();

        String text = component.toPlainText();

        if (component.getHoverEvent() != null) {
            HoverEvent hover = component.getHoverEvent();
            BaseComponent[] hoverValue = hover.getValue();
            String hoverText = serialize(hoverValue);
            result.append("<hover:").append(hover.getAction().name()).append(":").append(hoverText).append(">");
        }

        if (component.getClickEvent() != null) {
            ClickEvent click = component.getClickEvent();
            result.append("<click:").append(click.getAction().name().toLowerCase()).append(":").append(click.getValue()).append(">");
        }

        result.append(text);

        if (component.getClickEvent() != null) result.append("</click>");
        if (component.getHoverEvent() != null) result.append("</hover>");

        if (component.getExtra() != null) {
            for (BaseComponent extra : component.getExtra()) {
                result.append(serializeComponent(extra));
            }
        }

        return result.toString();
    }

}
