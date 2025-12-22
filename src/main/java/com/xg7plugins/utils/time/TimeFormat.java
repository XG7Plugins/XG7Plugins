package com.xg7plugins.utils.time;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Enumeration that provides different time formatting options.
 * Each format represents a specific way to display time durations,
 * from simple milliseconds to complex combinations of days, hours, minutes, seconds and milliseconds.
 */
@AllArgsConstructor
public enum TimeFormat {

    MILLISECONDS((t, n) -> t + n.get(0)),
    SECONDS((t, n) -> (t / 1000) + n.get(0)),
    MINUTES((t, n) -> (t / (1000 * 60)) + n.get(1)),
    HOURS((t, n) -> (t / (1000 * 60 * 60)) + n.get(2)),
    DAYS((t, n) -> (t / (1000 * 60 * 60 * 24)) + n.get(3)),

    SECONDS_MILLISECONDS((t, n) -> (t / 1000) + n.get(0) + " " + (t % 1000) + n.get(1)),

    MINUTES_SECONDS((t, n) -> (t / (1000 * 60)) + n.get(0) + " " + ((t % (1000 * 60)) / 1000) + n.get(1)),
    MINUTES_SECONDS_MILLISECONDS((t, n) -> {
        long minutes = t / (1000 * 60);
        long seconds = (t % (1000 * 60)) / 1000;
        long millis = t % 1000;
        return minutes + n.get(0) + " " + seconds + n.get(1) + " " + millis + n.get(2);
    }),

    HOURS_MINUTES((t, n) -> (t / (60 * 60 * 1000)) + n.get(0) + " " + ((t % (60 * 60 * 1000)) / (60 * 1000)) + n.get(1)),
    HOURS_MINUTES_SECONDS((t, n) -> {
        long hours = t / (60 * 60 * 1000);
        long minutes = (t % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (t % (60 * 1000)) / 1000;
        return hours + n.get(0) + " " + minutes + n.get(1) + " " + seconds + n.get(2);
    }),
    HOURS_MINUTES_SECONDS_MILLISECONDS((t, n) -> {
        long hours = t / (60 * 60 * 1000);
        long minutes = (t % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (t % (60 * 1000)) / 1000;
        long millis = t % 1000;
        return hours + n.get(0) + " " + minutes + n.get(1) + " " + seconds + n.get(2) + " " + millis + n.get(3);
    }),

    DAYS_HOURS((t, n) -> (t / (24 * 60 * 60 * 1000)) + n.get(0) + " " + ((t % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)) + n.get(1)),
    DAYS_HOURS_MINUTES((t, n) -> {
        long days = t / (24 * 60 * 60 * 1000);
        long hours = (t % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (t % (60 * 60 * 1000)) / (60 * 1000);
        return days + n.get(0) + " " + hours + n.get(1) + " " + minutes + n.get(2);
    }),
    DAYS_HOURS_MINUTES_SECONDS((t, n) -> {
        long days = t / (24 * 60 * 60 * 1000);
        long hours = (t % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (t % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (t % (60 * 1000)) / 1000;
        return days + n.get(0) + " " + hours + n.get(1) + " " + minutes + n.get(2) + " " + seconds + n.get(3);
    }),
    DAYS_HOURS_MINUTES_SECONDS_MILLISECONDS((t, n) -> {
        long days = t / (24 * 60 * 60 * 1000);
        long hours = (t % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (t % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (t % (60 * 1000)) / 1000;
        long millis = t % 1000;
        return days + n.get(0) + " " + hours + n.get(1) + " " + minutes + n.get(2) + " " + seconds + n.get(3) + " " + millis + n.get(4);
    });

    /**
     * Function that performs the actual time formatting.
     * Takes a time value in milliseconds and a list of placeholder strings,
     * returns the formatted time string.
     */
    private final BiFunction<Long, List<String>, String> formatter;

    /**
     * Formats a time duration according to this format's specification.
     * Uses placeholders from the main configuration file.
     *
     * @param time The time duration in milliseconds to format
     * @return A formatted string representation of the time duration
     */
    public String format(long time) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root();

        List<String> placeholders = new ArrayList<>();

        String thisName = this.name().toLowerCase();

        for (String name : thisName.split("_")) placeholders.add(config.get("time-placeholders." + name, name));

        return formatter.apply(time, placeholders);
    }


}
