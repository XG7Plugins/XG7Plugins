package com.xg7plugins.utils.text;

import com.xg7plugins.XG7Plugins;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponent {

    private static final Pattern pattern = Pattern.compile("\\[(CLICK|HOVER|CLICKHOVER) (.*?%)\\](.*?)\\[/\\1\\]", Pattern.DOTALL);
    private static final Pattern value = Pattern.compile("value=%(.*?)%");
    private static final Pattern textP = Pattern.compile("text=%(.*?)%");
    private static final Pattern action = Pattern.compile("action=%(.*?)%");

    private String text;

    private String rawText;


    public TextComponent(String text) {
        this.text = text;
        this.rawText = text.replaceAll("\\[(CLICK|HOVER|CLICKHOVER) (.*?%)\\](.*?)\\[/\\1\\]", "$3");
    }

    public BaseComponent[] getComponents() {

        if (XG7Plugins.getMinecraftVersion() < 8) {
            XG7Plugins.getInstance().getLog().warn("Versions lower than 1.8 don't have support to clickable or hover tags!");
        }

        String spaces = "";

        if (rawText.startsWith("[CENTER]")) {
            rawText = rawText.replace("[CENTER]", "");
            spaces = TextCentralizer.getSpacesCentralized(TextCentralizer.PixelsSize.CHAT.getPixels(), rawText);
        }

        Matcher matcher = pattern.matcher(text);
        int lastIndex = 0;
        ComponentBuilder builder = new ComponentBuilder(spaces);

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String outsideText = text.substring(lastIndex, matcher.start());
                builder.append(outsideText).reset();
            }

            String tagName = matcher.group(1);
            String attributes = matcher.group(2).trim();
            String content = matcher.group(3).trim();
            builder.append(content);

            Matcher valMatch = value.matcher(attributes);
            Matcher textMatch = textP.matcher(attributes);
            Matcher actionMatch = action.matcher(attributes);

            switch (tagName) {
                case "CLICK":
                    if (!valMatch.find() || !actionMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Click tag with content " + content + " has a syntax error!");
                        return null;
                    }
                    try {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.valueOf(actionMatch.group(1)),
                                Text.format(valMatch.group(1)).getText()));
                    } catch (IllegalArgumentException e) {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                Text.format(valMatch.group(1)).getText()));
                    }
                    break;

                case "HOVER":
                    if (!textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Hover tag with content " + content + " has a syntax error!");
                        return null;
                    }
                    builder.event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Text.format(valMatch.group(1)).getText()).create()));
                    break;

                case "CLICKHOVER":
                    if (!valMatch.find() || !actionMatch.find() || !textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Click and hover tag with content " + content + " has a syntax error!");
                        return null;
                    }
                    builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(textMatch.group(1)).create()));
                    try {
                        builder.event(new ClickEvent(
                                    ClickEvent.Action.valueOf(actionMatch.group(1)),
                                    Text.format(valMatch.group(1)).getText()
                                )
                        );
                    } catch (IllegalArgumentException e) {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                Text.format(valMatch.group(1)).getText()));
                    }
                    break;
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            String remainingText = text.substring(lastIndex);
            builder.append(remainingText).reset();
        }

        return builder.create();
    }

    public void send(CommandSender sender) {
        if (sender == null) return;

        if (text.isEmpty()) return;

        if (text.equals(rawText)) {
            Text.format(text).send(sender);
            return;
        }

        if (XG7Plugins.getMinecraftVersion() < 8) {
            if (rawText.startsWith("[CENTER]")) {
                rawText = rawText.replace("[CENTER]", "");
                rawText = TextCentralizer.getSpacesCentralized(TextCentralizer.PixelsSize.CHAT.getPixels(), rawText);
            }
            sender.sendMessage(rawText);
            return;
        }

        sender.spigot().sendMessage(getComponents());

    }

}
