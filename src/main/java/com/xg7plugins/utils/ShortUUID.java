package com.xg7plugins.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class ShortUUID {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom secureRandom = new SecureRandom();

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
