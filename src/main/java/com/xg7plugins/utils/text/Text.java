package com.xg7plugins.utils.text;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.utils.Pair;

import com.xg7plugins.utils.text.sender.TextSender;
import com.xg7plugins.utils.text.sender.deserializer.TextSenderDeserializer;
import com.xg7plugins.utils.time.TimeParser;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Getter
    private static BukkitAudiences audience;

    private TextSender textSender;
    @Setter
    private String text;

    public static void init() {
        audience = BukkitAudiences.create(XG7Plugins.getInstance());
    }

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
    public Text(Component component) {
        this(miniMessage.serialize(component));
    }

    /**
     * Processes the text specifically for a player, including:
     * - Condition processing
     * - PlaceholderAPI placeholders if available
     *
     * @param player The player to process text for
     * @return This Text instance for chaining
     */
    public Text textFor(Player player) {

        this.text = Condition.processConditions(this.text, player);

        if (XG7PluginsAPI.isDependencyEnabled("PlaceholderAPI")) this.text = ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, this.text));

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
    public final Text centralize(TextCentralizer.PixelsSize size) {
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

    public String getText() {
        return TimeParser.remainingTimeForValue(this.text);
    }

    public String getPlainText() {
        return LegacyComponentSerializer.legacyAmpersand().toBuilder().hexColors().build().serialize(getComponent());
    }

    public Component getComponent() {
        return miniMessage.deserialize(ColorTranslator.translateLegacyToMini(getText()));
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
    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText, boolean textForSender) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = rawText;

            text = text.replace("%prefix%", plugin.getEnvironmentConfig().getCustomPrefix())
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
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return new Text("Error: " + throwable.getMessage());
        });
    }
    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText) {
        return detectLangs(sender, plugin, rawText, true);
    }
    public static CompletableFuture<Text> fromLang(CommandSender sender, Plugin plugin, String path, boolean textForSender) {
        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {
            String text = lang.get(path);

            text = text.replace("%prefix%", plugin.getEnvironmentConfig().getCustomPrefix())
                    .replace("%player%", sender == null ? "No name" : sender.getName());

            Text objectText = new Text(text);

            if (sender instanceof Player && textForSender) objectText.textFor((Player) sender);

            return objectText;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return new Text("Error: " + throwable.getMessage());
        });
    }
    public static CompletableFuture<Text> fromLang(CommandSender sender, Plugin plugin, String path) {
        return fromLang(sender, plugin, path, true);
    }
    public static CompletableFuture<Void> sendTextFromLang(CommandSender sender, Plugin plugin, String path) {
        return fromLang(sender, plugin, path).thenAccept(text -> text.send(sender));
    }
    @SafeVarargs
    public static CompletableFuture<Void> sendTextFromLang(CommandSender sender, Plugin plugin, String path, Pair<String, String>... replacements) {
        return fromLang(sender, plugin, path).thenAccept(text -> text.replaceAll(replacements).send(sender));
    }
    public static CompletableFuture<Void> detectLangsAndSend(CommandSender sender, Plugin plugin, String rawText) {
        return detectLangs(sender, plugin, rawText).thenAccept(text -> text.send(sender));
    }
    @SafeVarargs
    public static CompletableFuture<Void> detectLangsAndSend(CommandSender sender, Plugin plugin, String rawText, Pair<String, String>... replacements) {
        return detectLangs(sender, plugin, rawText).thenAccept(text -> text.replaceAll(replacements).send(sender));
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
    public static Text format(Component component) {

        return new Text(component);
    }

}
