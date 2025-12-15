package com.xg7plugins.utils.location;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

@Getter
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
     * @param location1 The first corner point of the region
     * @param location2   The second corner point of the region
     * @throws IllegalArgumentException if the points are not in the same world
     */
    public Region(Location location1, Location location2) {
        setPoints(location1, location2);
    }

    public Region setPoints(Location location1, Location location2) {
        if (!location1.getWorldName().equals(location2.getWorldName())) {
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
        return this;
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

    public double getArea(Side side) {
        double dx = Math.abs(endPoint.getX() - startPoint.getX());
        double dy = Math.abs(endPoint.getY() - startPoint.getY());
        double dz = Math.abs(endPoint.getZ() - startPoint.getZ());

        switch (side) {
            case TOP:
            case BOTTOM:
                return dx * dz;
            case LEFT:
            case RIGHT:
                return dy * dz;
            case FRONT:
            case BACK:
                return dx * dy;
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
    public Region clone() {
        try {
            return (Region) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}