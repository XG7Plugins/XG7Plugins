package com.xg7plugins.utils.time;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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

    private final BiFunction<Long, List<String>, String> formatter;

    public String format(long time) {

        Config config = Config.mainConfigOf(XG7Plugins.getInstance());

        List<String> placeholders = new ArrayList<>();

        String thisName = this.name().toLowerCase();

        for (String name : thisName.split("_")) placeholders.add(config.get("time-placeholders." + name, String.class).orElse(name));

        return formatter.apply(time, placeholders);
    }


}
