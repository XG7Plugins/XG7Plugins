package com.xg7plugins.utils.text;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.utils.Pair;

import com.xg7plugins.utils.text.component.TextComponent;
import com.xg7plugins.utils.text.component.deserializer.ComponentDeserializer;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");

    private String text;

    public Text(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public Text textFor(Player player) {

        this.text = Condition.processCondition(this.text, player);

        if (XG7PluginsAPI.isDependencyEnabled("PlaceholderAPI")) this.text = PlaceholderAPI.setPlaceholders(player, this.text);

        return this;
    }

    public Text replace(String placeholder, String replacement) {
        this.text = this.text.replace("%" + placeholder + "%", replacement);
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
    public final Text centralize(TextCentralizer.PixelsSize size) {
        this.text = "[CENTER:" + size.name() + "]";
        return this;
    }
    public final Text centralize(int size) {
        this.text = "[CENTER:" + size + "]";
        return this;
    }

    public void send(CommandSender sender) {
        TextComponent component = ComponentDeserializer.deserialize(this.text);
        send(component, sender);
    }

    public static void send(TextComponent component, CommandSender sender) {
        component.send(sender);
    }

    public String getPlainText() {
        try {
            TextComponent component = ComponentDeserializer.deserialize(this.text);
            return component.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return this.text;
        }
    }

    public TextComponent getComponent() {
        return ComponentDeserializer.deserialize(this.text);
    }

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

    public static Text format(String text) {
        return new Text(text);
    }

}
