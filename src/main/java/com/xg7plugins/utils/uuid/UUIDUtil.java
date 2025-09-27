package com.xg7plugins.utils.uuid;

import java.util.UUID;

public class UUIDUtil {

    public static UUID addDashesToUUID(String uuidWithoutDashes) {
        if (uuidWithoutDashes == null || uuidWithoutDashes.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID: " + uuidWithoutDashes);
        }

        return UUID.fromString(uuidWithoutDashes.substring(0, 8) + "-" +
                uuidWithoutDashes.substring(8, 12) + "-" +
                uuidWithoutDashes.substring(12, 16) + "-" +
                uuidWithoutDashes.substring(16, 20) + "-" +
                uuidWithoutDashes.substring(20, 32));
    }

}
