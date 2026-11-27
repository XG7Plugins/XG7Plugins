package com.xg7plugins.api.event;

public enum EventPriority {

    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR;

    public static EventPriority fromString(String name) {
        for (EventPriority priority : values()) {
            if (priority.name().equalsIgnoreCase(name)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid event priority: " + name);
    }

    public static EventPriority fromEnum(Enum<?> enumValue) {
        return fromString(enumValue.name());
    }

    public static EventPriority fromBukkit(org.bukkit.event.EventPriority bukkitPriority) {
        switch (bukkitPriority) {
            case LOWEST:
                return LOWEST;
            case LOW:
                return LOW;
            case NORMAL:
                return NORMAL;
            case HIGH:
                return HIGH;
            case HIGHEST:
                return HIGHEST;
            case MONITOR:
                return MONITOR;
            default:
                throw new IllegalArgumentException("Invalid Bukkit event priority: " + bukkitPriority);
        }
    }

    public org.bukkit.event.EventPriority toBukkit() {
        switch (this) {
            case LOWEST:
                return org.bukkit.event.EventPriority.LOWEST;
            case LOW:
                return org.bukkit.event.EventPriority.LOW;
            case NORMAL:
                return org.bukkit.event.EventPriority.NORMAL;
            case HIGH:
                return org.bukkit.event.EventPriority.HIGH;
            case HIGHEST:
                return org.bukkit.event.EventPriority.HIGHEST;
            case MONITOR:
                return org.bukkit.event.EventPriority.MONITOR;
            default:
                throw new IllegalArgumentException("Invalid event priority: " + this);
        }
    }

}