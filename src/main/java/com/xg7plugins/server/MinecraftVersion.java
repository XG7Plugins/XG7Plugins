package com.xg7plugins.server;

import com.xg7plugins.XG7PluginsAPI;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to detect and compare Minecraft server versions.
 * Provides methods to check version compatibility and server package information.
 */
public class MinecraftVersion {
    @Getter
    private static final int version;
    private static final int version2;

    private static final String packageName;

    static {
        // Extract version number from server version string (e.g. "1.16" -> 16)
        Pattern pattern = Pattern.compile("1\\.([0-9]{1,2})(?:\\.([0-9]{1,2}))?");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());

        boolean find = matcher.find();

        if (!find) throw new RuntimeException("Failed to extract server version number");

        version = Integer.parseInt(matcher.group(1));
        version2 =  matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

        // Extract NMS package version name from the server package
        String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        // Doesn't work on 1.17+ on paper
        packageName = version.substring(version.lastIndexOf('.') + 1);
    }

    /**
     * Checks if the server version is older than a target version
     */
    public static boolean isOlderThan(int targetVersion) {
        return version < targetVersion;
    }

    /**
     * Checks if the server version is newer than a target version
     */
    public static boolean isNewerThan(int targetVersion) {
        return version > targetVersion;
    }

    /**
     * Checks if the server version is older than or equal to a target version
     */
    public static boolean isOlderOrEqual(int targetVersion) {
        return version <= targetVersion;
    }

    /**
     * Checks if the server version is newer than or equal to a target version
     */
    public static boolean isNewerOrEqual(int targetVersion) {
        return version >= targetVersion;
    }

    /**
     * Checks if the server version is older than a target version
     */
    public static boolean isOlderThan(int targetVersion, int targetVersion2) {
        return version < targetVersion && version2 < targetVersion2;
    }

    /**
     * Checks if the server version is newer than a target version
     */
    public static boolean isNewerThan(int targetVersion, int targetVersion2) {
        return version > targetVersion && version2 > targetVersion2;
    }

    /**
     * Checks if the server version is older than or equal to a target version
     */
    public static boolean isOlderOrEqual(int targetVersion, int targetVersion2) {
        return version <= targetVersion && version2 <= targetVersion2;
    }

    /**
     * Checks if the server version is newer than or equal to a target version
     */
    public static boolean isNewerOrEqual(int targetVersion, int targetVersion2) {
        return version >= targetVersion && version2 >= targetVersion2;
    }

    /**
     * Checks if the server version exactly matches a target version
     */
    public static boolean is(int targetVersion) {
        return targetVersion == version;
    }

    /**
     * Checks if the server version is between min and max versions (inclusive)
     */
    public static boolean isBetween(int minVersion, int maxVersion) {
        return minVersion <= version && version <= maxVersion;
    }

    /**
     * Checks if the server version exactly matches a target version
     */
    public static boolean is(int targetVersion, int targetVersion2) {
        return targetVersion == version && targetVersion2 == version2;
    }
    /**
     * Gets the NMS package version name
     *
     * @return NMS package version string
     * @throws UnsupportedOperationException if running Paper 1.17+
     */
    public static String getPackageName() {
        if (isNewerThan(16) && XG7PluginsAPI.getServerSoftware().isPaper()) throw new UnsupportedOperationException("This method is not supported on versions newer than 1.16 on paper");
        return packageName;
    }



}
