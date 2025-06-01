package com.xg7plugins.utils.time;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
public class Time {

    private long milliseconds;
    public static Time of(Long milliseconds) {
        if (milliseconds == null) return null;
        return new Time(milliseconds);
    }
    public static Time of(long milliseconds) {
        return new Time(milliseconds);
    }
    public static Time of(int seconds) {
        return new Time(seconds * 1000L);
    }
    public static Time of(int minutes, int seconds) {
        return new Time(minutes * 60000L + seconds * 1000L);
    }
    public static Time of(int hours, int minutes, int seconds) {
        return new Time(hours * 3600000L + minutes * 60000L + seconds * 1000L);
    }
    public static Time of(int days, int hours, int minutes, int seconds) {
        return new Time(days * 86400000L + hours * 3600000L + minutes * 60000L + seconds * 1000L);
    }
    public static Time ofString(String timePattern) {
        return new Time(TimeParser.convertToMilliseconds(timePattern));
    }
    public static Time ofDate(Date date) {
        return new Time(date.getTime());
    }
    public static Time ofTimeStamp(Timestamp timestamp) {
        return new Time(timestamp.getTime());
    }
    public static Time now() {
        return new Time(System.currentTimeMillis());
    }

    public Time add(Time time) {
        this.milliseconds += time.milliseconds;
        return this;
    }
    public Time subtract(Time time) {
        this.milliseconds -= time.milliseconds;
        return this;
    }
    public Time multiply(int factor) {
        this.milliseconds *= factor;
        return this;
    }
    public Time divide(int factor) {
        this.milliseconds /= factor;
        return this;
    }

    public Date toDate() {
        return new Date(milliseconds);
    }
    public Timestamp toTimeStamp() {
        return new Timestamp(milliseconds);
    }

    private Time() {}

    public static Time getRemainingTime(long time) {
        return new Time(time - System.currentTimeMillis());
    }
    public static Time getRemainingTime(Time time) {
        return getRemainingTime(time.getMilliseconds());
    }

    public String formatReamingTime(TimeFormat format) {
        return format.format(milliseconds);
    }
    public String formatDate(String format) {
        return new SimpleDateFormat().format(new Date(milliseconds));
    }

}
