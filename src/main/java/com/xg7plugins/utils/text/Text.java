package com.xg7plugins.utils.text;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.Pair;

import com.xg7plugins.utils.text.resolver.TagResolver;
import com.xg7plugins.utils.text.sender.TextSender;
import com.xg7plugins.utils.text.sender.deserializer.TextSenderDeserializer;
import com.xg7plugins.utils.time.TimeParser;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Text class handles text processing and manipulation for messages in the plugin.
 * It provides functionality for:
 * - Text formatting and color translation
 * - Placeholder replacement
 * - Language system integration
 * - Different types of text sending (action bar, chat, etc)
 */
@Getter
public class Text {

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");

    private TextSender textSender;
    @Setter
    private String text;

    /**
     * Creates a new Text instance from a string.
     * Extracts the text sender and translates color codes.
     *
     * @param text The raw text to process
     */
    public Text(String text) {
        Pair<TextSender, String> extracted = TextSenderDeserializer.extractSender(text);

        this.textSender = extracted.getFirst();
        this.text = ChatColor.translateAlternateColorCodes('&', extracted.getSecond());
    }

    public Text(BaseComponent[] components) {
        this(TagResolver.serialize(components));
    }

    /**
     * Processes the text specifically for a player, including
     * - Condition processing
     * - PlaceholderAPI placeholders if available
     *
     * @param player The player to process text for
     * @return This Text instance for chaining
     */
    public Text textFor(Player player) {

        if (XG7Plugins.getAPI().isDependencyEnabled("PlaceholderAPI")) this.text = ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, this.text));

        this.text = Condition.processConditions(this.text, player);

        return this;
    }

    /**
     * Sets the text sender for this text instance
     *
     * @param sender The TextSender to use
     * @return This Text instance for chaining
     */
    public Text setSender(TextSender sender) {
        this.textSender = sender;
        return this;
    }

    /**
     * Replaces a single placeholder in the text
     *
     * @param placeholder The placeholder to replace (without % symbols)
     * @param replacement The replacement text
     * @return This Text instance for chaining
     */
    public Text replace(String placeholder, String replacement, boolean translateColors) {
        this.text = this.text.replace("%" + placeholder + "%", translateColors ? ChatColor.translateAlternateColorCodes('&', replacement) : replacement);
        return this;
    }

    public Text replace(String placeholder, String replacement) {
        return replace(placeholder, replacement, true);
    }

    public Text replaceLiteral(String s, String replacement) {
        this.text = this.text.replace(s, replacement);
        return this;
    }

    @SafeVarargs
    public final Text replaceAll(Pair<String, String>... replacements) {
        if (replacements == null) return this;
        Arrays.stream(replacements).forEach(replacement -> this.text = this.text.replace("%" + replacement.getFirst() + "%", replacement.getSecond()));
        return this;
    }
    public final Text replaceAll(List<Pair<String,String>> replacements) {
        if (replacements == null) return this;
        replacements.forEach(replacement -> this.text = this.text.replace("%" + replacement.getFirst() + "%", replacement.getSecond()));
        return this;
    }
    public final Text centralize(Text.PixelsSize size) {
        this.text = "[CENTER:" + size.name() + "]";
        return Text.format(text);
    }
    public final Text centralize(int size) {
        this.text = "[CENTER:" + size + "]";
        return Text.format(text);
    }

    public final Text appendStart(String s) {
        this.text = s + this.text;
        return this;
    }
    public final Text append(String s) {
        this.text = this.text + s;
        return this;
    }

    public final Text appendStart(Text text) {
        this.text = text.getText() + this.text;
        return this;
    }
    public final Text append(Text text) {
        this.text = this.text + text.getText();
        return this;
    }

    public List<Text> split(String regex) {

        List<Text> texts = new ArrayList<>();

        String[] split = getTextRaw().split(regex);

        for (String s : split) {
            texts.add(new Text(s));
        }

        System.out.println("Texts: " + texts);

        return texts;
    }

    /**
     * Sends the text to a command sender using the text's TextSender
     *
     * @param text   The text to send
     * @param sender The recipient of the message
     */
    public static void send(Text text, CommandSender sender) {
        text.getTextSender().send(sender, text);
    }

    public void send(CommandSender sender) {
        send(this, sender);
    }

    /**
     * Gets the raw unformatted text string
     *
     * @return The raw text without any processing
     */
    public String getTextRaw() {
        return this.text;
    }

    /**
     * Gets the text with all color codes and formatting tags removed
     *
     * @return Plain text without colors or formatting
     */
    public String getPlainText() {
        return net.md_5.bungee.api.ChatColor.stripColor(TagResolver.removeTags(this.text));
    }

    /**
     * Gets the processed text in legacy format
     * For MC < 1.8, removes tags only
     * For MC >= 1.8, converts to a component, then converts the component to
     * legacy text format
     *
     * @return Text formatted for the current MC version
     */
    public String getText() {
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) return TagResolver.removeTags(this.text);
        return new TextComponent(getComponent()).toLegacyText();
    }

    /**
     * Gets the text converted into BaseComponents with remaining time placeholders parsed
     *
     * @return Array of BaseComponents representing the text
     */
    public BaseComponent[] getComponent() {
        return TagResolver.deserialize(TimeParser.remainingTimeForValue(getTextRaw()));
    }

    public Component toAdventureComponent() {
        return LegacyComponentSerializer.legacySection().deserialize(getText());
    }

    /**
     * Detects and processes language placeholders in the text
     *
     * @param sender        The command sender
     * @param plugin        The plugin instance
     * @param rawText       The text to process
     * @param textForSender Whether to process the text specifically for the sender
     * @return CompletableFuture containing the processed Text
     */
    public static Text detectLangs(CommandSender sender, Plugin plugin, String rawText, boolean textForSender) {

        if (rawText == null) rawText = "null";

        Lang lang = Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).getSecond();

        String text = rawText;

        text = text.replace("%prefix%", plugin.getCustomPrefix())
                .replace("%player%", sender == null ? "No name" : sender.getName());

        Matcher langMatch = LANG_PATTERN.matcher(text);

        StringBuilder result = new StringBuilder(text);

        while (langMatch.find()) {
            String path = langMatch.group(1);
            result.replace(langMatch.start(), langMatch.end(), lang.get(path));
        }

        Text objectText = new Text(result.toString());

        if (sender instanceof Player && textForSender) objectText.textFor((Player) sender);

        return objectText;

    }

    public static Text detectLangs(CommandSender sender, Plugin plugin, String rawText) {
        return detectLangs(sender, plugin, rawText, true);
    }
    public static Text fromLang(CommandSender sender, Plugin plugin, @NotNull String path, boolean textForSender) {

        Lang lang = Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).getSecond();

        String text = lang.get(path);

        text = text.replace("%prefix%", plugin.getCustomPrefix())
                .replace("%player%", sender == null ? "No name" : sender.getName());

        Text objectText = new Text(text);

        if (sender instanceof Player && textForSender) objectText.textFor((Player) sender);

        return objectText;
    }

    public static Text fromLang(CommandSender sender, Plugin plugin, String path) {
        return fromLang(sender, plugin, path, true);
    }
    public static void sendTextFromLang(CommandSender sender, Plugin plugin, String path) {
        fromLang(sender, plugin, path).send(sender);
    }
    @SafeVarargs
    public static void sendTextFromLang(CommandSender sender, Plugin plugin, String path, Pair<String, String>... replacements) {
        fromLang(sender, plugin, path).replaceAll(replacements).send(sender);
    }
    public static void detectLangsAndSend(CommandSender sender, Plugin plugin, String rawText) {
        detectLangs(sender, plugin, rawText).send(sender);
    }
    @SafeVarargs
    public static void detectLangsAndSend(CommandSender sender, Plugin plugin, String rawText, Pair<String, String>... replacements) {
        detectLangs(sender, plugin, rawText).replaceAll(replacements).send(sender);
    }

    /**
     * Creates a new Text instance with formatted text
     *
     * @param text The text to format
     * @return A new Text instance
     */
    public static Text format(String text) {
        return new Text(text);
    }

    public static Text format(BaseComponent[] text) {
        return new Text(text);
    }

    /**
     * Enum representing different contexts where a text can be centralized,
     * with their corresponding pixel widths.
     */
    @Getter
    public enum PixelsSize {

        CHAT(157), // Chat message width
        MOTD(127), // Server MOTD width
        INV(75),   // Inventory name width
        BOOK_LINE(114);

        final int pixels;

        PixelsSize(int pixels) {
            this.pixels = pixels;
        }

    }

    /**
     * Gets the pixel width of a character, accounting for bold formatting.
     * Characters are grouped by their width in pixels.
     *
     * @param c      The character to measure
     * @param isBold Whether the character is boldly formatted
     * @return The pixel width of the character
     */
    public static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{
                "~@", //7px bold -> 8px
                "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", //6px bold -> 7px
                "{}fk*\"<>()", //5px bold -> 6px
                "It[] ", //4px bold -> 5px
                "'l`", //3px bold -> 4px
                "!|:;,.i", //2px bold -> 3px
                "¨´" //1px bold -> 2px
        };
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) {
                return isBold && c != ' ' ? 8 - i : 7 - i;
            }
        }

        return 4;
    }

    //Text width in Minecraft pixels
    public static int getTextWidth(String text) {
        int textWidth = 0;
        boolean isBold = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§' || c == '&') {
                if (i + 1 >= text.length()) continue;
                char next = text.charAt(i + 1);

                if (next == 'x' && i + 13 < text.length()) {
                    i += 13;
                    continue;
                }

                if (next == '#' && i + 7 < text.length()) {
                    i += 7;
                    continue;
                }

                if (next == 'l' || next == 'L') {
                    isBold = true;
                } else if (next == 'r' || next == 'R') {
                    isBold = false;
                }

                i++;
                continue;
            }

            textWidth += Text.getCharSize(c, isBold);
        }

        return textWidth;
    }

    @Override
    public String toString() {
        return this.text;
    }


}
