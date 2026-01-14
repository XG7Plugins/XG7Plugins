package com.xg7plugins.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {

    private static final Pattern VAR_PATTERN = Pattern.compile("(global:)?\\{([a-zA-Z0-9_.-]+)}");


    private static final HashMap<UUID, HashMap<String, String>> playerVariables = new HashMap<>();
    private static final HashMap<String, String> globalVariables = new HashMap<>();

    public static void setGlobal(String key, String value) {
        globalVariables.put(key, value);
    }

    public static void removeGlobal(String key) {
        globalVariables.remove(key);
    }

    public static String getGlobal(String key, String def) {
        return globalVariables.getOrDefault(key, def);
    }

    public static void setPlayer(UUID playerId, String key, String value) {
        playerVariables
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(key, value);
    }

    public static void removePlayer(UUID playerId, String key) {
        if (playerVariables.containsKey(playerId)) {
            playerVariables.get(playerId).remove(key);
        }
    }

    public static String getPlayer(UUID playerId, String key, String def) {
        return playerVariables
                .getOrDefault(playerId, new HashMap<>())
                .getOrDefault(key, def);
    }

    public static void clearPlayer(UUID playerId) {
        playerVariables.remove(playerId);
    }

    public static void clearGlobal() {
        globalVariables.clear();
    }

    public static String replaceGlobalVariables(String text) {
        return replaceAllVariables(null, text);
    }


    public static String replaceAllVariables(UUID playerId, String text) {

        Matcher matcher = VAR_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        Map<String, String> playerVars =
                playerVariables.getOrDefault(playerId, new HashMap<>());

        while (matcher.find()) {

            boolean forceGlobal = matcher.group(1) != null;
            String key = matcher.group(2);

            String value = null;

            if (!forceGlobal) {
                value = playerVars.get(key);
            }
            if (value == null) {
                value = globalVariables.get(key);
            }
            if (value == null) {
                value = "null";
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(result);
        return result.toString();
    }


    public static List<Pair<String, String>> getAllGlobalVariables() {
        return Pair.getListFromEntrySet(globalVariables.entrySet());
    }
    public static List<Pair<String, String>> getAllPlayerVariables(UUID playerId) {
        if (!playerVariables.containsKey(playerId)) return Collections.emptyList();
        return Pair.getListFromEntrySet(playerVariables.get(playerId).entrySet());
    }


}
