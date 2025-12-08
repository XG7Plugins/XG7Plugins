package com.xg7plugins.server;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import lombok.Getter;

/**
 * Utility class to detect and compare Minecraft server versions.
 * Provides methods to check version compatibility and server package information.
 */
public class MinecraftServerVersion {

    @Getter
    private static final String packageName;

    static {
        // Extract NMS package version name from the server package
        String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        // Doesn't work on 1.17+ on paper
        packageName = version.substring(version.lastIndexOf('.') + 1);
    }

    /**
     * Checks if the server version is older than a target version
     */
    public static boolean isOlderThan(ServerVersion targetVersion) {
        return PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(targetVersion);
    }

    /**
     * Checks if the server version is newer than a target version
     */
    public static boolean isNewerThan(ServerVersion targetVersion) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(targetVersion);
    }

    /**
     * Checks if the server version is older than or equal to a target version
     */
    public static boolean isOlderOrEqual(ServerVersion targetVersion) {
        return PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(targetVersion);
    }

    /**
     * Checks if the server version is newer than or equal to a target version
     */
    public static boolean isNewerOrEqual(ServerVersion targetVersion) {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(targetVersion);
    }

}