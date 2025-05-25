package com.xg7plugins.utils.location;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

@Getter
@Setter

/**
 * Represents a 3D rectangular region in a Minecraft world defined by two corner points.
 * This class provides functionality to check if locations are within the region and
 * calculate various geometric properties.
 */
public class Region implements Cloneable {

    private Location startPoint;
    private Location endPoint;

    /**
     * Represents a region with the specified corner points.
     * The points are automatically sorted so that startPoint contains the minimum coordinates
     * and endPoint contains the maximum coordinates.
     *
     * @param startPoint The first corner point of the region
     * @param endPoint   The second corner point of the region
     * @throws IllegalArgumentException if the points are not in the same world
     */
    public Region(Location startPoint, Location endPoint) {
        if (!startPoint.getWorldName().equals(endPoint.getWorldName())) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }
        this.startPoint = new Location(
                startPoint.getWorldName(),
                Math.min(startPoint.getX(), endPoint.getX()),
                Math.min(startPoint.getY(), endPoint.getY()),
                Math.min(startPoint.getZ(), endPoint.getZ())
        );
        this.endPoint = new Location(
                startPoint.getWorldName(),
                Math.max(startPoint.getX(), endPoint.getX()),
                Math.max(startPoint.getY(), endPoint.getY()),
                Math.max(startPoint.getZ(), endPoint.getZ())
        );
    }

    /**
     * Checks if a location is inside this region.
     *
     * @param location The location to check
     * @return true if the location is inside the region, false otherwise
     */
    public boolean isInside(Location location) {
        return endPoint.getX() >= location.getX() && startPoint.getX() <= location.getX() &&
                endPoint.getY() >= location.getY() && startPoint.getY() <= location.getY() &&
                endPoint.getZ() >= location.getZ() && startPoint.getZ() <= location.getZ();
    }

    /**
     * Checks if an entity is inside this region.
     *
     * @param entity The entity to check
     * @return true if the entity is inside the region, false otherwise
     */
    public boolean isInside(Entity entity) {
        return isInside(Location.fromBukkit(entity.getLocation()));
    }
    @Override
    public String toString() {
        return "Region{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }

    /**
     * Gets the length of the region along the specified orientation.
     *
     * @param orientation The orientation (X, Y, or Z axis)
     * @return The length of the region in blocks along the specified orientation
     */
    public double side(Orientation orientation) {
        switch (orientation) {
            case X:
                return (int) endPoint.getX() - startPoint.getX();
            case Y:
                return (int) endPoint.getY() - startPoint.getY();
            case Z:
                return (int) endPoint.getZ() - startPoint.getZ();
            default:
                return 0;
        }
    }

    /**
     * Calculates the area of the region's face in the specified direction.
     *
     * @param direction The direction of the face (NORTH, SOUTH, EAST, or WEST)
     * @return The area of the face in square blocks
     */
    public double getArea(Direction direction) {
        switch (direction) {
            case NORTH:
            case SOUTH:
                return Math.abs(endPoint.getX() - startPoint.getX()) * Math.abs(endPoint.getZ() - startPoint.getZ());
            case EAST:
            case WEST:
                return Math.abs(endPoint.getY() - startPoint.getY()) * Math.abs(endPoint.getZ() - startPoint.getZ());
            default:
                return 0;
        }
    }

    /**
     * Calculates the volume of the region.
     *
     * @return The volume in cubic blocks
     */
    public double getVolume() {
        return (endPoint.getX() - startPoint.getX()) * (endPoint.getY() - startPoint.getY()) * (endPoint.getZ() - startPoint.getZ());
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Represents the three possible orientations in 3D space.
     */
    public enum Orientation {
        X, Y, Z
    }

    /**
     * Represents the four cardinal directions.
     */
    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

}