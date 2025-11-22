package com.xg7plugins.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;

import java.util.function.Function;

/**
 * An enum that provides parsing functionality for converting strings into various data types.
 * Each enum constant represents a specific type conversion operation.
 */
public enum Parser {
    INTEGER(Integer::parseInt),
    STRING(s -> s),
    BOOLEAN(s -> s.equalsIgnoreCase("true") || (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && s.equals(PlaceholderAPIPlugin.booleanTrue()))),
    LONG(Long::parseLong),
    DOUBLE(Double::parseDouble),
    FLOAT(Float::parseFloat),
    SHORT(Short::parseShort),
    BYTE(Byte::parseByte),
    CHAR(s -> s.charAt(0));

    private final Function<String, ?> converter;

    Parser(Function<String, ?> converter) {
        this.converter = converter;
    }

    /**
     * Converts a string value to the target type using the associated converter function.
     *
     * @param <T>   The target type to convert to
     * @param value The string value to convert
     * @return The converted value of type T
     */
    public <T> T convert(String value) {
        if (value == null) return null;
        return (T) converter.apply(value);
    }

    public static <T> Parser getParserOf(Class<T> type) {
        try {
            Parser.valueOf(type.getSimpleName().toUpperCase());
        } catch (Exception ig) {
            return null;
        }
        return null;
    }
}
