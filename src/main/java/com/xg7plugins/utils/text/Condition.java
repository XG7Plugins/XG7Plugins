package com.xg7plugins.utils.text;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents conditional text processing for player-specific messages.
 * This enum provides different types of conditions that can be used to show/hide text based on certain criteria.
 * <p>
 * Usage format: "?CONDITION_TYPE: value? text"
 */
@AllArgsConstructor
@Getter
public enum Condition {

    /**
     * Checks if a boolean condition is true.
     * Example: "?IF: %player_flying%? Player is flying"
     * Example: "?IF: true? This text will show"
     */
    IF((conditionPack) -> {
        try {
            return Parser.BOOLEAN.convert(conditionPack.conditionValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    /**
     * Checks if a boolean condition is false.
     * Example: "?IF_NOT: %player_flying%? Player is not flying"
     * Example: "?IF_NOT: false? This text will show"
     */
    IF_NOT((conditionPack) -> {
        try {
            return !((boolean) Parser.BOOLEAN.convert(conditionPack.conditionValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    /**
     * Checks if a player has a specific permission.
     * Example: "?PERMISSION: example.permission? Player has permission"
     * Example: "?PERMISSION: admin.access? You are an admin"
     */
    PERMISSION((conditionPack -> conditionPack.getPlayer().hasPermission(conditionPack.getConditionValue()))),
    /**
     * Checks if a player does not have a specific permission.
     * Example: "?NO_PERMISSION: vip.access? You are not VIP"
     * Example: "?NO_PERMISSION: admin.access? You are not an admin"
     */
    NO_PERMISSION((conditionPack) -> !conditionPack.getPlayer().hasPermission(conditionPack.getConditionValue()));

    private final Function<ConditionPack, Boolean> condition;

    public boolean apply(ConditionPack pack) {
        return condition.apply(pack);
    }

    private static final Pattern conditionPattern = Pattern.compile("\\?(.*?): (.*?)\\? ");

    /**
     * Extracts the condition and its value from a text string.
     * The condition format should be "?CONDITION_TYPE: value?"
     *
     * @param condition The text containing the condition to extract
     * @return A Pair containing the Condition enum and its associated value, or null if no valid condition is found
     */
    public static Pair<Condition, String> extractCondition(String condition) {
        Matcher matcher = conditionPattern.matcher(condition);
        if (matcher.find()) {
            String type = matcher.group(1);
            for (Condition value : values()) {
                if (value.name().equalsIgnoreCase(type)) {
                    return new Pair<>(value, matcher.group(2));
                }
            }
        }
        return null;
    }

    /**
     * Processes a line of text containing a condition and returns the appropriate result.
     * If the condition is met, returns the text without the condition tags.
     * If the condition is not met, returns an empty string.
     * If no condition is found, returns the original line unchanged.
     *
     * @param line   The text line to process
     * @param player The player to check the condition against
     * @return The processed text result
     */
    public static String processCondition(String line, Player player) {
        Pair<Condition, String> extracted = extractCondition(line);
        if (extracted == null) return line;

        if (!extracted.getFirst().apply(new ConditionPack(player, extracted.getSecond()))) return "";

        return conditionPattern.matcher(line).replaceAll("");
    }

    public static String removeTags(String text) {
        return conditionPattern.matcher(text).replaceAll("");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static
    public class ConditionPack {
        private Player player;
        private String conditionValue;
    }
}

