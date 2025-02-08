package com.xg7plugins.utils.text;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.utils.Condition;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.clip.placeholderapi.libs.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final BukkitAudiences audience = BukkitAudiences.create(XG7Plugins.getInstance());

    String text;
    String rawText;

    public Text(String text) {

        this.text = text;

        String legacyText = LegacyComponentSerializer.legacyAmpersand().serialize(MINI_MESSAGE.deserialize(text));

        this.rawText = PlainTextComponentSerializer.plainText().serialize(Component.text(legacyText));

    }

    public Text replace(String placeholder, String replacement) {
        this.text = this.text.replace(placeholder, replacement);
        this.rawText = this.rawText.replace(placeholder, replacement);

        return this;
    }

    public Text textFor(Player player) {

        this.text = Condition.processCondition(this.text, player);

        if (XG7Plugins.isPlaceholderAPI()) this.text = PlaceholderAPI.setPlaceholders(player, this.text);

        return this;
    }

    public Component toAdventureComponent() {
        return MINI_MESSAGE.deserialize(text.replace("[CENTER] ", "").replace("[ACTION] ", ""));
    }

    public String getCentralizedText(TextCentralizer.PixelsSize pixelsSize) {
        return TextCentralizer.getCentralizedText(pixelsSize, rawText);
    }

    public void send(CommandSender sender) {

        if (text.isEmpty()) return;

        boolean isCenter = false;

        if (text.startsWith("[CENTER] ")) {
            this.text = text.substring(9);
            this.rawText = rawText.substring(9);
            isCenter = true;
        }

        if (!(sender instanceof Player)) {
            this.rawText = Condition.removeTags(rawText);
            if (isCenter) this.rawText = getCentralizedText(TextCentralizer.PixelsSize.CHAT);
            sender.sendMessage(rawText);
            return;
        }

        boolean isActionBar = false;

        if (text.startsWith("[ACTION] ")) {
            this.text = text.substring(9);
            this.rawText = rawText.substring(9);
            isActionBar = true;
        }

        if (isCenter && !isActionBar) {this.text = getCentralizedText(TextCentralizer.PixelsSize.CHAT);}

        if (isActionBar) audience.player((Player) sender).sendActionBar((ComponentLike) MINI_MESSAGE.deserialize(text));
        else audience.player((Player) sender).sendMessage((ComponentLike) MINI_MESSAGE.deserialize(text));

    }

    public static Text format(String text) {
        return new Text(text);
    }

    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = rawText;

            text = text.replace("[PLUGIN]", plugin.getCustomPrefix());
            text = text.replace("[PLAYER]", sender.getName());

            Matcher langMatch = LANG_PATTERN.matcher(text);

            StringBuilder result = new StringBuilder(text);

            while (langMatch.find()) {
                String path = langMatch.group(1);

                result.replace(langMatch.start(), langMatch.end(), lang.get(path));
            }

            return new Text(result.toString());
        });
    }

}
