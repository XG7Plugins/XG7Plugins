package com.xg7plugins.utils.text;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponent {

    private static final Pattern pattern = Pattern.compile("\\[(CLICK|HOVER|CLICKHOVER) (.*?%)\\](.*?)\\[/\\1\\]", Pattern.DOTALL);
    private static final Pattern value = Pattern.compile("value=%(.*?)%");
    private static final Pattern textP = Pattern.compile("text=%(.*?)%");
    private static final Pattern action = Pattern.compile("action=%(.*?)%");


    private final String text;
    private final String rawText;

    private final Plugin plugin;

    private final HashMap<String, String> replacements = new HashMap<>();

    public TextComponent(String text, Plugin plugin) {

        String rawText = text.replaceAll("\\[(CLICK|HOVER|CLICKHOVER) (.*?%)\\](.*?)\\[/\\1\\]", "$3");
        if (!rawText.equals(text) && XG7Plugins.getMinecraftVersion() < 8) {
            plugin.getLog().warn("Versions lower than 1.8 don't have support to clickable or hover tags!");
        }
        this.text = text;
        this.rawText = rawText;

        this.plugin = plugin;

    }

    public TextComponent replace(String placeholder, String replacement) {
        replacements.put(placeholder,replacement);
        return this;
    }

    public void send(CommandSender sender) {
        System.out.println(replacements);
        if (sender == null) return;
        if (!(sender instanceof Player)) {
            // Envia texto centralizado simples para não-jogadores
            sender.sendMessage(rawText.startsWith("[CENTER] ")
                    ? Text.getSpacesCentralized(Text.PixelsSize.CHAT.getPixels(), rawText) +
                    Text.format(rawText.substring(9), plugin).getText()
                    : Text.format(rawText, plugin).getText());
            return;
        }

        Player player = (Player) sender;

        String translatedRawText = Text.getWithPlaceholders(plugin, rawText, player);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            translatedRawText = translatedRawText.replace(entry.getKey(), entry.getValue());
        }

        String spaces = Text.getSpacesCentralized(Text.PixelsSize.CHAT.getPixels(), translatedRawText);

        if (XG7Plugins.getMinecraftVersion() < 8) {
            player.sendMessage(spaces + translatedRawText);
            return;
        }

        String finalText = text.startsWith("[CENTER] ") ? Text.getWithPlaceholders(plugin, text.substring(9), player) : Text.getWithPlaceholders(plugin, text, player);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            finalText = finalText.replace(entry.getKey(), entry.getValue());
        }

        Matcher matcher = pattern.matcher(finalText);
        int lastIndex = 0;
        ComponentBuilder builder = new ComponentBuilder(spaces);

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String outsideText = finalText.substring(lastIndex, matcher.start());
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
                        return;
                    }
                    try {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.valueOf(actionMatch.group(1)),
                                Text.format(valMatch.group(1), plugin).setReplacements(replacements).getWithPlaceholders(player)));
                    } catch (IllegalArgumentException e) {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                Text.format(valMatch.group(1), plugin).setReplacements(replacements).getWithPlaceholders(player)));
                    }
                    break;

                case "HOVER":
                    if (!textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Hover tag with content " + content + " has a syntax error!");
                        return;
                    }
                    builder.event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Text.format(textMatch.group(1), plugin).setReplacements(replacements).getWithPlaceholders(player)).create()));
                    break;

                case "CLICKHOVER":
                    if (!valMatch.find() || !actionMatch.find() || !textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Click and hover tag with content " + content + " has a syntax error!");
                        return;
                    }
                    builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(textMatch.group(1)).create()));
                    try {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.valueOf(actionMatch.group(1)),
                                Text.format(valMatch.group(1), plugin).setReplacements(replacements).getWithPlaceholders(player)));
                    } catch (IllegalArgumentException e) {
                        builder.event(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                Text.format(valMatch.group(1), plugin).setReplacements(replacements).getWithPlaceholders(player)));
                    }
                    break;
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < finalText.length()) {
            String remainingText = finalText.substring(lastIndex);
            builder.append(remainingText).reset();
        }

        player.spigot().sendMessage(builder.create());
    }



}
