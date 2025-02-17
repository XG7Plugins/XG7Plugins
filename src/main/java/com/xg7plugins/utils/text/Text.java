package com.xg7plugins.utils.text;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.utils.Condition;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.ComponentDeserializer;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");
    @Getter
    private String text;

    public Text(String text) {
        this.text = text;
    }

    public Text textFor(Player player) {

        this.text = Condition.processCondition(this.text, player);

        if (XG7Plugins.isPlaceholderAPI()) this.text = PlaceholderAPI.setPlaceholders(player, this.text);

        return this;
    }

    public Text replace(String placeholder, String replacement) {
        this.text = this.text.replace("%" + placeholder + "%", replacement);
        return this;
    }
    @SafeVarargs
    public final Text replace(Pair<String, String>... replacements) {
        for (Pair<String,String> replacement : replacements) {
            this.text = this.text.replace("%" + replacement.getFirst() + "%", replacement.getSecond());
        }
        return this;
    }


    public void send(CommandSender sender) {

        if (this.text.isEmpty()) return;

        Component component = ComponentDeserializer.deserialize(this.text);

        String rawText = component.content();
        String componentText = component.getText();

        boolean isCentered = rawText.startsWith("<center> ");
        boolean isAction = rawText.startsWith("<action> ");
        boolean isPlayer = sender instanceof Player;

        componentText = componentText.replace("<center> ", "");
        componentText = componentText.replace("<action> ", "");

        component.setText(componentText);

        if (isCentered) component.center(TextCentralizer.PixelsSize.CHAT);

        if (!isPlayer) {
            if (XG7Plugins.getMinecraftVersion() < 8) {
                sender.sendMessage(rawText);
                return;
            }
            sender.spigot().sendMessage(component.toBukkitComponent());
            return;
        }

        Player player = (Player) sender;

        if (isAction) {
            if (XG7Plugins.getMinecraftVersion() < 8) return;

            if (XG7Plugins.getMinecraftVersion() > 8) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rawText));
                return;
            }

            WrapperPlayServerChatMessage packetPlayOutChat = new WrapperPlayServerChatMessage(
                    new ChatMessageLegacy(net.kyori.adventure.text.Component.text(rawText), ChatTypes.GAME_INFO)
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutChat);

            return;
        }

        if (XG7Plugins.getMinecraftVersion() < 8) {
            player.sendMessage(rawText);
            return;
        }

        player.spigot().sendMessage(component.toBukkitComponent());

    }

    public String getRawText() {

        Component component = ComponentDeserializer.deserialize(this.text);

        String rawText = component.content();

        if (rawText.startsWith("<center> ")) rawText = rawText.replace("<center> ", "");
        if (rawText.startsWith("<action> ")) rawText = rawText.replace("<action> ", "");

        return rawText;
    }

    public Component getComponent() {
        return ComponentDeserializer.deserialize(this.text);
    }

    public String getCentralizedText(TextCentralizer.PixelsSize size) {

        Component component = ComponentDeserializer.deserialize(this.text);

        String rawText = component.content();

        if (rawText.startsWith("<center> ")) rawText = rawText.replace("<center> ", "");
        if (rawText.startsWith("<action> ")) rawText = rawText.replace("<action> ", "");

        return TextCentralizer.getCentralizedText(size, rawText);
    }

    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = rawText;

            text = text.replace("%plugin%", plugin.getCustomPrefix());
            text = text.replace("%prefix%", sender.getName());

            Matcher langMatch = LANG_PATTERN.matcher(text);

            StringBuilder result = new StringBuilder(text);

            while (langMatch.find()) {
                String path = langMatch.group(1);

                result.replace(langMatch.start(), langMatch.end(), lang.get(path));
            }

            Text objectText = new Text(result.toString());

            if (sender instanceof Player) {
                objectText.textFor((Player) sender);
            }

            return objectText;
        });
    }
    public static CompletableFuture<Text> fromLang(CommandSender sender, Plugin plugin, String path) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = lang.get(path);

            text = text.replace("%plugin%", plugin.getCustomPrefix());
            text = text.replace("%prefix%", sender.getName());

            Text objectText = new Text(text);

            if (sender instanceof Player) {
                objectText.textFor((Player) sender);
            }

            return objectText;
        });
    }

    public static Text format(String text) {
        return new Text(text);
    }

}
