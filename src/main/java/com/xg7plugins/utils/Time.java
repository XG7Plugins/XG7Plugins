package com.xg7plugins.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(ms|[SMHD])", Pattern.CASE_INSENSITIVE);


    public static long convertToMilliseconds(String timeStr) throws TimeParseException {
        long milliseconds = 0;
        Matcher matcher = TIME_PATTERN.matcher(timeStr.toUpperCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    milliseconds += value * 1000;
                    break;
                case "M":
                    milliseconds += value * 60000;
                    break;
                case "H":
                    milliseconds += value * 3600000;
                    break;
                case "D":
                    milliseconds += value * 86400000;
                    break;
                case "MS":
                    milliseconds += value;
                    break;
                default:
                    throw new TimeParseException("Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }

    public static long convertTicksToMillis(long ticks) {
        return ticks * 50;
    }
    public static long convertMillisToTicks(long millis) {
        return millis / 50;
    }

    public static class TimeParseException extends Exception {
        public TimeParseException(String message) {
            super(message);
        }
    }


}
