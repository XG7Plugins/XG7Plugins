package com.xg7plugins.utils.text;

/**
 * Utility class for parsing custom color and formatting codes into ANSI escape codes.
 */
public class AnsiParser {

    /**
     * Parses a message containing custom color and formatting codes (prefixed with '§')
     * and converts them into ANSI escape codes for terminal display.
     *
     * @param message The input message with custom codes.
     * @return The message with ANSI escape codes.
     */
    public static String parse(String message) {
        if (message == null) return "";

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

}
