package com.xg7plugins.utils.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for time-related operations and conversions.
 * Provides methods to parse time strings and convert between different time units.
 */
public class TimeParser {

    /**
     * Pattern to match time strings in the format: number + unit
     * Valid units are: MS (milliseconds), S (seconds), M (minutes), H (hours), D (days)
     * Example: "5H" = 5 hours, "30M" = 30 minutes, "100MS" = 100 milliseconds
     */
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(MS|[SMHD])", Pattern.CASE_INSENSITIVE);

    private static final Pattern REMAINING_TIME_PATTERN = Pattern.compile("@(\\w+):\\s*(\\d+)@");


    /**
     * Converts a time string to milliseconds.
     * Accepts multiple time units in a single string (e.g., "1H30M" = 1 hour and 30 minutes)
     *
     * @param timeStr the time string to convert
     * @return the time in milliseconds
     * @throws TimeParseException if the time string format is invalid
     */
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

    public static String remainingTimeForValue(String str) {
        Matcher matcher = REMAINING_TIME_PATTERN.matcher(str);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            try {
                String formatName = matcher.group(1).toUpperCase();
                long millis = Long.parseLong(matcher.group(2));

                TimeFormat format = TimeFormat.valueOf(formatName);

                matcher.appendReplacement(result, Matcher.quoteReplacement(format.format(millis)));

            } catch (IllegalArgumentException e) {
                throw new TimeParseException("Illegal time format or time value. Expected: @TIME_FORMAT:milli@ Found: " + matcher.group());
            }
        }

        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Converts Minecraft server ticks to milliseconds
     * 1 tick = 50 milliseconds
     *
     * @param ticks number of ticks to convert
     * @return equivalent time in milliseconds
     */
    public static long convertTicksToMillis(long ticks) {
        return ticks * 50;
    }

    /**
     * Converts milliseconds to Minecraft server ticks
     * 50 milliseconds = 1 tick
     *
     * @param millis time in milliseconds to convert
     * @return equivalent number of ticks
     */
    public static long convertMillisToTicks(long millis) {
        return millis / 50;
    }

    /**
     * Exception thrown when there's an error parsing time strings
     */
    public static class TimeParseException extends IllegalArgumentException {
        public TimeParseException(String message) {
            super(message);
        }
    }


}
