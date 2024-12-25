package com.xg7plugins.commands.setup;

import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.utils.Parser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor
public class CommandArgs {

    private final String[] args;

    @SneakyThrows
    public <T> T get(int index, Class<T> type) {

        if (index >= args.length) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds!");
        }

        if (type.isAssignableFrom(OfflinePlayer.class)) {
            return (T) Bukkit.getOfflinePlayer( args[index]);
        }

        if (type == Integer.class || type == int.class) return Parser.INTEGER.convert(args[index]);
        if (type == String.class) return (T) args[index];
        if (type == Boolean.class || type == boolean.class) return Parser.BOOLEAN.convert(args[index]);
        if (type == Long.class || type == long.class) return Parser.LONG.convert(args[index]);
        if (type == Double.class || type == double.class) return Parser.DOUBLE.convert(args[index]);
        if (type == Float.class || type == float.class) return Parser.FLOAT.convert(args[index]);
        if (type == Short.class || type == short.class) return Parser.SHORT.convert(args[index]);
        if (type == Byte.class || type == byte.class) return Parser.BYTE.convert(args[index]);
        if (type == Character.class || type == char.class) return Parser.CHAR.convert(args[index]);

        return null;
    }
}
