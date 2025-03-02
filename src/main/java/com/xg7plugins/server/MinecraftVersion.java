package com.xg7plugins.server;

import com.xg7plugins.XG7Plugins;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersion {
    @Getter
    private static final int version;

    private static final String packageName;

    static {
        Pattern pattern = Pattern.compile("1\\.([0-9]?[0-9])");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
        version = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;

        String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        // Does't work on 1.17+ on paper
        packageName = version.substring(version.lastIndexOf('.') + 1);
    }

    public static boolean isOlderThan(int targetVersion) {
        return version < targetVersion;
    }
    public static boolean isNewerThan(int targetVersion) {
        return version > targetVersion;
    }
    public static boolean isOlderOrEqual(int targetVersion) {
        return version <= targetVersion;
    }
    public static boolean isNewerOrEqual(int targetVersion) {
        return version >= targetVersion;
    }
    public static boolean is(int targetVersion) {
        return targetVersion == version;
    }
    public static boolean isBetween(int minVersion, int maxVersion) {
        return minVersion <= version && version <= maxVersion;
    }

    public static String getPackageName() {
        if (isNewerThan(16) && XG7Plugins.serverInfo().getSoftware().isPaper()) throw new UnsupportedOperationException("This method is not supported on versions newer than 1.16 on paper");
        return packageName;
    }



}
