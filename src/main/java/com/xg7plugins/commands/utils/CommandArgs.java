package com.xg7plugins.commands.utils;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.utils.time.TimeParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.function.Consumer;

/**
 * Utility class for handling and parsing command arguments.
 * Provides type-safe access to command arguments with automatic conversion.
 */
@Getter
@AllArgsConstructor
public class CommandArgs {

    private final String[] args;

    /**
     * Gets the length of the arguments array.
     *
     * @return The number of arguments
     */
    public int len() {
        return args.length;
    }

    /**
     * Retrieves and converts an argument at the specified index to the requested type.
     * Supports conversion to various types including:
     * - Bukkit types (OfflinePlayer, World)
     * - Plugin instances
     * - Primitive types and their wrappers
     *
     * @param index The position of the argument to retrieve
     * @param type  The class type to convert the argument to
     * @param <T>   The generic type parameter
     * @return The converted argument value, or null if conversion is not supported
     * @throws IllegalArgumentException if the index is out of bounds
     */
    @SneakyThrows
    public <T> T get(int index, Class<T> type) {

        if (index >= args.length) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds!");
        }

        if (OfflinePlayer.class.isAssignableFrom(type)) return type.cast(Bukkit.getOfflinePlayer(args[index]));
        if (World.class.isAssignableFrom(type)) return type.cast(Bukkit.getWorld(args[index]));
        if (Plugin.class.isAssignableFrom(type)) return type.cast(XG7Plugins.getAPI().getXG7Plugin(args[index]));
        if (Time.class.isAssignableFrom(type)) return type.cast(Time.of(TimeParser.convertToMilliseconds(args[index])));

        if (type == Integer.class || type == int.class) return Parser.INTEGER.convert(args[index]);
        if (type == String.class) return Parser.STRING.convert(args[index]);
        if (type == Boolean.class || type == boolean.class) return Parser.BOOLEAN.convert(args[index]);
        if (type == Long.class || type == long.class) return Parser.LONG.convert(args[index]);
        if (type == Double.class || type == double.class) return Parser.DOUBLE.convert(args[index]);
        if (type == Float.class || type == float.class) return Parser.FLOAT.convert(args[index]);
        if (type == Short.class || type == short.class) return Parser.SHORT.convert(args[index]);
        if (type == Byte.class || type == byte.class) return Parser.BYTE.convert(args[index]);
        if (type == Character.class || type == char.class) return Parser.CHAR.convert(args[index]);

        return null;
    }

    /**
     * Converts all arguments to a single space-separated string.
     *
     * @return A string containing all arguments joined with spaces
     */
    @Override
    public String toString() {
        return String.join(" ", args);
    }

    public void forEach(Consumer<String> consumer) {
        for (String arg : args) {
            consumer.accept(arg);
        }
    }
}
