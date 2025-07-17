package com.xg7plugins.utils.time;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class for handling time-related operations and conversions.
 * Provides methods to create, manipulate and format time values in various units.
 */
@Data
@AllArgsConstructor
public class Time {

    /**
     * The time value stored in milliseconds
     */
    private long milliseconds;

    /**
     * Creates a Time instance from nullable milliseconds value
     */
    public static Time of(Long milliseconds) {
        if (milliseconds == null) return null;
        return new Time(milliseconds);
    }

    /**
     * Creates a Time instance from milliseconds
     */
    public static Time of(long milliseconds) {
        return new Time(milliseconds);
    }

    /**
     * Creates a Time instance from seconds
     */
    public static Time of(int seconds) {
        return new Time(seconds * 1000L);
    }

    /**
     * Creates a Time instance from minutes and seconds
     */
    public static Time of(int minutes, int seconds) {
        return new Time(minutes * 60000L + seconds * 1000L);
    }

    /**
     * Creates a Time instance from hours, minutes and seconds
     */
    public static Time of(int hours, int minutes, int seconds) {
        return new Time(hours * 3600000L + minutes * 60000L + seconds * 1000L);
    }

    /**
     * Creates a Time instance from days, hours, minutes and seconds
     */
    public static Time of(int days, int hours, int minutes, int seconds) {
        return new Time(days * 86400000L + hours * 3600000L + minutes * 60000L + seconds * 1000L);
    }

    /**
     * Creates a Time instance from a time pattern string
     */
    public static Time ofString(String timePattern) {
        return new Time(TimeParser.convertToMilliseconds(timePattern));
    }

    /**
     * Creates a Time instance from a Date object
     */
    public static Time ofDate(Date date) {
        return new Time(date.getTime());
    }

    /**
     * Creates a Time instance from a Timestamp
     */
    public static Time ofTimeStamp(Timestamp timestamp) {
        return new Time(timestamp.getTime());
    }

    /**
     * Creates a Time instance with current system time
     */
    public static Time now() {
        return new Time(System.currentTimeMillis());
    }

    /**
     * Adds another Time value to this one
     */
    public Time add(Time time) {
        this.milliseconds += time.milliseconds;
        return this;
    }

    /**
     * Subtracts another Time value from this one
     */
    public Time subtract(Time time) {
        this.milliseconds -= time.milliseconds;
        return this;
    }

    /**
     * Multiplies this Time value by a factor
     */
    public Time multiply(int factor) {
        this.milliseconds *= factor;
        return this;
    }

    /**
     * Divides this Time value by a factor
     */
    public Time divide(int factor) {
        this.milliseconds /= factor;
        return this;
    }

    /**
     * Checks if this Time is before another Time
     */
    public boolean isBefore(Time time) {
        return milliseconds < time.milliseconds;
    }

    /**
     * Checks if this Time is after another Time
     */
    public boolean isAfter(Time time) {
        return milliseconds > time.milliseconds;
    }

    /**
     * Checks if this Time equals another Time
     */
    public boolean isEqual(Time time) {
        return milliseconds == time.milliseconds;
    }

    /**
     * Checks if this Time is equal to or before another Time
     */
    public boolean isEqualOrBefore(Time time) {
        return milliseconds <= time.milliseconds;
    }

    /**
     * Checks if this Time is equal to or after another Time
     */
    public boolean isEqualOrAfter(Time time) {
        return milliseconds >= time.milliseconds;
    }

    /**
     * Checks if this Time represents zero duration
     */
    public boolean isZero() {
        return milliseconds == 0;
    }

    /**
     * Converts this Time to a Date object
     */
    public Date toDate() {
        return new Date(milliseconds);
    }

    /**
     * Converts this Time to a Timestamp
     */
    public Timestamp toTimeStamp() {
        return new Timestamp(milliseconds);
    }

    private Time() {
    }

    /**
     * Gets remaining time from a future timestamp
     */
    public static Time getRemainingTime(long time) {
        return new Time(time - System.currentTimeMillis());
    }

    /**
     * Gets remaining time from a future Time
     */
    public static Time getRemainingTime(Time time) {
        return getRemainingTime(time.getMilliseconds());
    }

    /**
     * Formats remaining time using a specified format
     */
    public String formatReamingTime(TimeFormat format) {
        return format.format(milliseconds);
    }

    /**
     * Formats this Time as a date string using a specified format
     */
    public String formatDate(String format) {
        return new SimpleDateFormat().format(new Date(milliseconds));
    }

}