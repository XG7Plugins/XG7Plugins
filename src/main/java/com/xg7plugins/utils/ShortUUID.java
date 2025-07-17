package com.xg7plugins.utils;

import java.security.SecureRandom;

/**
 * Utility class for generating short unique identifiers using base62 encoding.
 * The generated IDs always start with '#' followed by random base62 characters.
 */
public class ShortUUID {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a short unique identifier of a specified length
     *
     * @param size The total length of the ID to generate (including the '#' prefix)
     * @return A string starting with '#' followed by random base62 characters
     */
    public static String generateUUID(int size) {
        StringBuilder sb = new StringBuilder(size);
        sb.append("#");
        for (int i = 1; i < size; i++) {
            int index = secureRandom.nextInt(BASE62.length());
            sb.append(BASE62.charAt(index));
        }
        return sb.toString();
    }

}
