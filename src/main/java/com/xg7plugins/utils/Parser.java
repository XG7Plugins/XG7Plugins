package com.xg7plugins.utils;

import com.xg7plugins.utils.time.Time;
import com.xg7plugins.utils.time.TimeFormat;
import com.xg7plugins.utils.time.TimeParser;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;
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
    TIME(Time::ofString),
    UUID(java.util.UUID::fromString),
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

    /**
     * Retrieves the Parser enum constant corresponding to the specified class type.
     *
     * @param <T>  The target type
     * @param type The class type to get the parser for
     * @return The corresponding Parser enum constant, or null if not found
     */
    public static <T> Parser getParserOf(Class<T> type) {

        try {
            return Parser.valueOf(type.getSimpleName().toUpperCase());
        } catch (Exception parseException) {
            throw new RuntimeException(parseException);
        }
    }

    public boolean isBoolean() {
        return this == Parser.BOOLEAN;
    }

    public boolean isNumber() {
        return this == Parser.INTEGER || this == Parser.LONG ||  this == Parser.DOUBLE || this == Parser.FLOAT ||  this == Parser.SHORT;
    }

    public boolean isTime() {
        return this == Parser.TIME;
    }
}
