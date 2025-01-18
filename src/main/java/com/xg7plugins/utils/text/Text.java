package com.xg7plugins.utils.text;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.utils.Condition;

import lombok.Getter;
import lombok.SneakyThrows;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private String text;

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");


    public Text(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);

        if (XG7Plugins.getMinecraftVersion() >= 16) {
            this.text = Color.hex(this.text);
            this.text = Color.gradient(this.text);
        }
    }

    public Text replace(String placeholder, String replacement) {
        this.text = this.text.replace(placeholder, replacement);
        return this;
    }
    public Text replaceAll(HashMap<String, String> replacements) {
        for (String placeholder : replacements.keySet()) {
            this.text = this.text.replace(placeholder, replacements.get(placeholder));
        }
        return this;
    }

    public Text setPlaceholders(Player player) {
        this.text = XG7Plugins.isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(player, text) : text;
        return this;
    }

    public boolean processCondition(Player player) {
        return !Objects.equals(Condition.processCondition(text, player), "");
    }

    public Text condition(Player player) {
        this.text = Condition.processCondition(text, player);
        return this;
    }

    public String getTextFor(CommandSender sender) {
        if (sender instanceof Player) {
            return setPlaceholders((Player) sender).condition((Player) sender).getText();
        }
        return text;
    }

    public Text textFor(CommandSender sender) {
        if (sender instanceof Player) {
            setPlaceholders((Player) sender).condition((Player) sender).getText();
        }
        return this;
    }

    public String getTextCentralized(TextCentralizer.PixelsSize pixelsSize) {
        if (!text.startsWith("[CENTER] ")) return text;
        text = text.substring(9);
        return TextCentralizer.getCentralizedText(pixelsSize, text);
    }

    public String getTextForCentralized(TextCentralizer.PixelsSize pixelsSize, CommandSender sender) {
        if (!text.startsWith("[CENTER] ")) return text;
        text = text.substring(9);
        if (sender instanceof Player) {
            return TextCentralizer.getCentralizedText(pixelsSize, setPlaceholders((Player) sender).condition((Player) sender).getText());
        }
        return TextCentralizer.getCentralizedText(pixelsSize, text);
    }

    public com.xg7plugins.utils.text.TextComponent toComponent() {
        return new com.xg7plugins.utils.text.TextComponent(text);
    }

    public static Text format(String text) {
        return new Text(text);
    }

    public static CompletableFuture<Text> formatLang(Plugin plugin, @NotNull CommandSender sender, String langPath) {

        return CompletableFuture.supplyAsync(() -> {

            Config lang = null;

            LangManager langManager = XG7Plugins.getInstance().getLangManager();

            if (langManager == null) lang = XG7Plugins.getInstance().getConfigsManager().getConfig("messages");

            if (lang == null) lang = Config.of(plugin, langManager.getLangByPlayer(plugin, (sender instanceof Player) ? (Player) sender : null).join());

            return Text.format(lang.get(langPath, String.class).orElse("Cannot found path \"" + langPath + "\" in " + lang.get("formated-name", String.class).orElse("langs")).replace("[PREFIX]", plugin.getCustomPrefix())).textFor(sender);

        });

    }

    public static CompletableFuture<Text> detectLangOrText(Plugin plugin, @NotNull CommandSender sender, String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String finalText = text;

                Matcher matcher = LANG_PATTERN.matcher(finalText);

                while (matcher.find()) {

                    Config lang = null;
                    LangManager langManager = XG7Plugins.getInstance().getLangManager();

                    if (langManager == null) {
                        lang = XG7Plugins.getInstance().getConfigsManager().getConfig("messages");
                    }

                    if (lang == null) {
                        lang = Config.of(plugin, langManager.getLangByPlayer(plugin, (sender instanceof Player) ? (Player) sender : null).join());
                    }

                    StringBuilder result = new StringBuilder(finalText);

                    String langPath = matcher.group(1);

                    String replacement = lang.get(langPath, String.class)
                            .orElse("Cannot found path \"" + langPath + "\" in " + lang.get("formated-name", String.class).orElse("langs"));

                    result.replace(matcher.start(), matcher.end(), replacement);

                    finalText = result.toString();
                }

                return Text.format(finalText.replace("[PREFIX]", plugin.getCustomPrefix())).textFor(sender);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        });
    }


    public void send(CommandSender sender) {
        if (sender == null) return;


        if (text.isEmpty()) return;



        if (sender instanceof Player) {
            if (text.startsWith("[ACTION] ")) {
                text = text.substring(9);
                sendActionBar((Player) sender);
                return;
            }
        }

        if (text.startsWith("[CENTER] ")) {
            text = text.substring(9);
            String centralizedText = TextCentralizer.getCentralizedText(TextCentralizer.PixelsSize.CHAT, text);
            sender.sendMessage(centralizedText);
            return;
        }

        String textForSender = getTextFor(sender);
        sender.sendMessage(textForSender);
    }

    @SneakyThrows
    public void sendActionBar(Player player) {

        if (XG7Plugins.getMinecraftVersion() < 8) return;

        XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().add(player.getUniqueId());
        sendScoreActionBar(player);

        Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().remove(player.getUniqueId()),60L);

    }

    @SneakyThrows
    public void sendScoreActionBar(Player player) {

        if (XG7Plugins.getMinecraftVersion() < 8) return;

        if (XG7Plugins.getMinecraftVersion() > 8) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
            return;
        }

        WrapperPlayServerChatMessage packet = new WrapperPlayServerChatMessage(
                new ChatMessageLegacy(Component.text(text), ChatTypes.GAME_INFO)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

    }


    public static long convertToMilliseconds(Plugin plugin, String timeStr) {
        long milliseconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)(ms|[SMHD])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeStr.toUpperCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    milliseconds += value * 1000;
                    break;
                case "M":
                    milliseconds += value * 60000;
                    break;
                case "H":
                    milliseconds += value * 3600000;
                    break;
                case "D":
                    milliseconds += value * 86400000;
                    break;
                case "MS":
                    milliseconds += value;
                    break;
                default:
                    plugin.getLog().severe("Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }

}
