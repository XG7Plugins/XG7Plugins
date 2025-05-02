package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Getter
public enum Condition {

    IF((conditionPack) -> {
        try {
            return Parser.BOOLEAN.convert(conditionPack.conditionValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    IF_NOT((conditionPack) -> {
        try {
            return !((boolean) Parser.BOOLEAN.convert(conditionPack.conditionValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }),
    PERMISSION((conditionPack -> conditionPack.getPlayer().hasPermission(conditionPack.getConditionValue()))),
    NO_PERMISSION((conditionPack) -> !conditionPack.getPlayer().hasPermission(conditionPack.getConditionValue()));

    private final Function<ConditionPack, Boolean> condition;

    public boolean apply(ConditionPack pack) {
        return condition.apply(pack);
    }

    private static final Pattern conditionPattern = Pattern.compile("\\[(.*?): (.*?)\\] ");

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

