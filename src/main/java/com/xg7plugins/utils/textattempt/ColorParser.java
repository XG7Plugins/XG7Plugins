package com.xg7plugins.utils.textattempt;

/**
 * Utility class for parsing custom color and formatting codes into ANSI escape codes.
 */
public class ColorParser {

    /**
     * Parses a message containing custom color and formatting codes (prefixed with '§')
     * and converts them into ANSI escape codes for terminal display.
     *
     * @param message The input message with custom codes.
     * @return The message with ANSI escape codes.
     */
    public static String parseAnsi(String message) {
        if (message == null || message.isEmpty()) return "";

        return message
                .replace("§0", "\u001B[30m") //black
                .replace("§1", "\u001B[34m") // dark blue
                .replace("§2", "\u001B[32m") // dark green
                .replace("§3", "\u001B[36m") // dark aqua
                .replace("§4", "\u001B[31m") // dark red
                .replace("§5", "\u001B[35m") // dark purple
                .replace("§6", "\u001B[33m") // gold
                .replace("§7", "\u001B[37m") // gray
                .replace("§8", "\u001B[90m") // dark gray
                .replace("§9", "\u001B[94m") // blue
                .replace("§a", "\u001B[92m") // green
                .replace("§b", "\u001B[96m") // aqua
                .replace("§c", "\u001B[91m") // red
                .replace("§d", "\u001B[95m") // light purple
                .replace("§e", "\u001B[93m") // yellow
                .replace("§f", "\u001B[97m") // white

                .replace("§l", "\u001B[1m")  // bold
                .replace("§n", "\u001B[4m")  // underline
                .replace("§o", "\u001B[3m")  // italic
                .replace("§m", "\u001B[9m")  // strikethrough
                .replace("§r", "\u001B[0m"); // reset
    }

    public static String parseAdventure(String message) {
        if (message == null || message.isEmpty()) return "";

        return message
                .replace("§0", "<black>") //black
                .replace("§1", "<dark_blue>") // dark blue
                .replace("§2", "<dark_green>") // dark green
                .replace("§3", "<dark_aqua>") // dark aqua
                .replace("§4", "<dark_red>") // dark red
                .replace("§5", "<dark_purple>") // dark purple
                .replace("§6", "<gold>") // gold
                .replace("§7", "<gray>") // gray
                .replace("§8", "<dark>") // dark gray
                .replace("§9", "<blue>") // blue
                .replace("§a", "<green>") // green
                .replace("§b", "<aqua>") // aqua
                .replace("§c", "<red>") // red
                .replace("§d", "<light_purple>") // light purple
                .replace("§e", "<yellow>") // yellow
                .replace("§f", "<white>") // white

                .replace("§k", "<obfuscated>")  // obfuscated
                .replace("§l", "<bold>")  // bold
                .replace("§n", "<underline>")  // underline
                .replace("§o", "<italic>")  // italic
                .replace("§m", "<strikethrough>")  // strikethrough
                .replace("§r", "<reset>"); // reset
    }



}
