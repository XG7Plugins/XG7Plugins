package com.xg7plugins.utils.location;

import lombok.Getter;
import org.bukkit.entity.Entity;

/**
 * Enum representing cardinal and intercardinal directions with associated yaw values.
 */
@Getter
public enum Direction {

    NORTH(180),
    SOUTH(0),
    EAST(270),
    WEST(90),

    NORTH_EAST(225f),
    NORTH_WEST(135f),
    SOUTH_EAST(315f),
    SOUTH_WEST(45f);

    private final float yaw;

    Direction(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Determines the closest Direction based on a given yaw angle.
     *
     * @param yaw The yaw angle in degrees.
     * @return The closest Direction.
     */
    public static Direction fromYaw(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        Direction closest = null;
        float minDiff = Float.MAX_VALUE;

        for (Direction dir : values()) {
            float diff = Math.abs(yaw - dir.yaw);
            diff = Math.min(diff, 360 - diff);

            if (diff < minDiff) {
                minDiff = diff;
                closest = dir;
            }
        }

        return closest;
    }

    /**
     * Determines the Direction of a given entity based on its yaw.
     *
     * @param entity The entity whose direction is to be determined.
     * @return The Direction the entity is facing.
     */
    public static Direction fromEntity(Entity entity) {
        return fromYaw(entity.getLocation().getYaw());
    }


}
