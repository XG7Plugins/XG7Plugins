package com.xg7plugins.utils.text;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("\\[g#([0-9a-fA-F]{6})\\](.*?)\\[/g#([0-9a-fA-F]{6})\\]");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");
    private static ReflectionObject packetPlayOutChat;

    static {
        packetPlayOutChat = XG7Plugins.getMinecraftVersion() < 13 ? NMSUtil.getNMSClass("PacketPlayOutChat").newInstance() : null;
    }



    private String text;
    private final Plugin plugin;
    private HashMap<String, String> replacements = new HashMap<>();

    public Text(String text, Plugin plugin) {
        this.text = text;
        if (XG7Plugins.getMinecraftVersion() >= 16) {
            this.text = applyGradients();
            Matcher matcher = HEX_PATTERN.matcher(this.text);
            while (matcher.find()) {
                String color = this.text.substring(matcher.start(), matcher.end());
                this.text = this.text.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)) + "");
                matcher = HEX_PATTERN.matcher(this.text);
            }
        }

        this.plugin = plugin;

        this.text = ChatColor.translateAlternateColorCodes('&', this.text.replace("[PREFIX]", plugin.getCustomPrefix()));
    }

    public static Text format(String text, Plugin plugin) {
        return new Text(text,plugin);
    }
    public static com.xg7plugins.utils.text.TextComponent formatComponent(String text, Plugin plugin) {
        Text text1 = new Text(text, plugin);
        return new com.xg7plugins.utils.text.TextComponent(text1.getText(),plugin);
    }

    public Text setReplacements(HashMap<String, String> replacements) {
        this.replacements = replacements;
        return this;
    }

    public String getText() {
        String finalText = text;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            finalText = finalText.replace(entry.getKey(),entry.getValue());
        }
        return finalText;
    }

    public String getWithPlaceholders(Player player) {
        YamlConfiguration entity = this.plugin.getLangManager().getLangByPlayer(player);
        String textToTraslate = getText();
        Matcher matcher = LANG_PATTERN.matcher(textToTraslate);
        while (matcher.find()) {
            String lang = matcher.group(1);
            if (entity.getString(lang) == null) {
                String translation = plugin.getLangManager().getLang(plugin.getLangManager().getMainLang()).getString(lang);
                if (translation == null) {
                    textToTraslate = "Not found path \"" + lang + "\" in langs";
                    continue;
                }
                textToTraslate = textToTraslate.replace(textToTraslate.substring(matcher.start(), matcher.end()), translation);

                continue;
            }
            textToTraslate = textToTraslate.replace(textToTraslate.substring(matcher.start(), matcher.end()), entity.getString(lang));
        }
        textToTraslate = textToTraslate.replace("[PLAYER]", player.getName());

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            textToTraslate = textToTraslate.replace(entry.getKey(),entry.getValue());
        }

        return Text.format(XG7Plugins.isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders((OfflinePlayer) player, textToTraslate) : textToTraslate, plugin).getText();
    }

    public static String getWithPlaceholders(Plugin plugin, String text, Player player) {

        YamlConfiguration entity = plugin.getLangManager().getLangByPlayer(player);

        String textToTraslate = text;

        Matcher matcher = LANG_PATTERN.matcher(textToTraslate);

        while (matcher.find()) {
            String lang = matcher.group(1);
            textToTraslate = textToTraslate.replace(textToTraslate.substring(matcher.start(), matcher.end()), entity.getString(lang));
        }

        textToTraslate = textToTraslate.replace("[PLAYER]", player.getName());

        return Text.format(XG7Plugins.isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders((OfflinePlayer) player, textToTraslate) : textToTraslate, plugin).getText();
    }

    public Text replace(String placeholder, String replacement) {
        replacements.put(placeholder,replacement);
        return this;
    }


    public void send(CommandSender sender) {
        if (text == null || text.isEmpty()) return;

        if (sender instanceof Player) {

            String transleted = getWithPlaceholders((Player) sender);

            transleted = transleted.replace("[PLAYER]", sender.getName());
            if (transleted.startsWith("[ACTION] ")) {
                sendActionBar(((Player) sender));
                return;
            }

            sender.sendMessage(getCentralizedText(PixelsSize.CHAT.pixels, transleted));
            return;
        }

        String textToTraslate = text;

        Matcher matcher = LANG_PATTERN.matcher(textToTraslate);

        YamlConfiguration mainLang = plugin.getLangManager().getLang(plugin.getLangManager().getMainLang());

        while (matcher.find()) {
            textToTraslate = textToTraslate.replace(textToTraslate.substring(matcher.start(), matcher.end()), mainLang.getString(matcher.group(1)));
        }

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            textToTraslate = textToTraslate.replace(entry.getKey(),entry.getValue());
        }

        sender.sendMessage(getCentralizedText(PixelsSize.CHAT.pixels, textToTraslate));
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

        String finalText = getWithPlaceholders(player);

        if (text.startsWith("[ACTION] ")) finalText = finalText.substring(9);

        if (XG7Plugins.getMinecraftVersion() > 8) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(finalText));
            return;
        }

        ReflectionObject chatComponent = NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(finalText);

        packetPlayOutChat.setField("a",chatComponent.getObject());
        packetPlayOutChat.setField("b",(byte) 2);

        PlayerNMS.cast(player).sendPacket(packetPlayOutChat.getObject());

    }

    public static String getCentralizedText(int pixels, String text) {
        if (!text.startsWith("[CENTER] ")) return text;
        return getSpacesCentralized(pixels,text) + text.substring(9);

    }


    public static String getSpacesCentralized(int pixels, String text) {


        if (!text.startsWith("[CENTER] ")) return "";

        text = text.substring(9);


        int textWidht = 0;
        boolean cCode = false;
        boolean isBold = false;
        boolean isrgb = false;
        int rgbCount = 0;
        int cCodeCount = 0;
        int rgbToAdd = 0;
        for (char c : text.toCharArray()) {
            if (isrgb) {
                if (rgbCount == 6) {
                    isrgb = false;
                    continue;
                }
                if ("0123456789aAbBcCdDeEfF".contains(String.valueOf(c))) {
                    rgbToAdd = getCharSize(c, isBold);
                    rgbCount++;
                    continue;
                }
                rgbCount = 0;
                textWidht += rgbToAdd;
                continue;
            }
            if (c == '&' || c == '§') {
                cCode = true;
                cCodeCount++;
                continue;
            }
            if (cCode && net.md_5.bungee.api.ChatColor.ALL_CODES.contains(c + "")) {
                cCode = false;
                cCodeCount = 0;
                isBold = c == 'l' || c == 'L';
                continue;
            }
            if (cCode) {
                if (c == '#') {
                    cCode = false;
                    isrgb = true;
                    continue;
                }
                while (cCodeCount != 0) {
                    cCodeCount--;
                    textWidht += getCharSize('&', isBold);
                }
            }
            textWidht += getCharSize(c, isBold);
        }

        textWidht /= 2;

        if (textWidht > pixels) return text;

        StringBuilder builder = new StringBuilder();

        int compensated = 0;

        while (compensated < pixels - textWidht) {
            builder.append(ChatColor.COLOR_CHAR + "r ");
            compensated += 4;
        }

        return builder.toString();

    }

    private double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    private String applyGradients() {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            Color from = Color.decode("#" + matcher.group(1));
            Color to = Color.decode("#" + matcher.group(3));
            String textHex = matcher.group(2);

            double[] red = linear(from.getRed(), to.getRed(), textHex.length());
            double[] green = linear(from.getGreen(), to.getGreen(), textHex.length());
            double[] blue = linear(from.getBlue(), to.getBlue(), textHex.length());

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < textHex.length(); i++) {
                builder.append(net.md_5.bungee.api.ChatColor.of(new Color(
                                (int) Math.round(red[i]),
                                (int) Math.round(green[i]),
                                (int) Math.round(blue[i]))))
                        .append(textHex.charAt(i));
            }
            matcher.appendReplacement(result, builder.toString());
        }
        matcher.appendTail(result);

        return result.toString() + net.md_5.bungee.api.ChatColor.RESET;
    }
    private static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{"~@", "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", "{}fk*\"<>()", "It[] ", "'l`", "!|:;,.i", "¨´"};
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) {
                return isBold && c != ' ' ? 8 - i : 7 - i;
            }
        }

        return 4;
    }


    @Getter
    public enum PixelsSize {

        CHAT(157),
        MOTD(127),
        INV(75);

        final int pixels;

        PixelsSize (int pixels) {
            this.pixels = pixels;
        }

    }

    public static long convertToMilliseconds(Plugin plugin, String timeStr) {
        long milliseconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)([SMHD])");
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
                default:
                    plugin.getLog().severe("Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }


}
