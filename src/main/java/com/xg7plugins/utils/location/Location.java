package com.xg7plugins.utils.location;

import com.github.retrooper.packetevents.util.Vector3d;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Represents a location in a Minecraft world with coordinates and rotation.
 * This class provides methods for manipulating and working with 3D positions.
 */
@AllArgsConstructor
@Getter
public class Location implements Cloneable {

    /**
     * The name of the world this location is in
     */
    private String world;
    /**
     * X coordinate
     */
    private double x;
    /**
     * Y coordinate
     */
    private double y;
    /**
     * Z coordinate
     */
    private double z;
    /**
     * Yaw rotation (left/right)
     */
    private float yaw;
    /**
     * Pitch rotation (up/down)
     */
    private float pitch;

    /**
     * Creates a location with default rotation values (0,0)
     *
     * @param world World name
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public Location(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    /**
     * Gets the Bukkit World object for this location
     *
     * @return The Bukkit World
     */
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * Gets the name of the world
     *
     * @return World name
     */
    public String getWorldName() {
        return world;
    }

    /**
     * Adds the given coordinates to this location
     *
     * @param x X coordinate to add
     * @param y Y coordinate to add
     * @param z Z coordinate to add
     * @return This location object
     */
    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Adds the given coordinates and rotation to this location
     * @param x X coordinate to add
     * @param y Y coordinate to add
     * @param z Z coordinate to add
     * @param yaw Yaw rotation to add
     * @param pitch Pitch rotation to add
     * @return This location object
     */
    public Location add(double x, double y, double z,float yaw, float pitch) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.yaw += yaw;
        this.pitch += pitch;
        return this;
    }
    /**
     * Adds the given location's coordinates and rotation to this location
     *
     * @param locationToAdd Location to add
     * @return This location object
     */
    public Location add(Location locationToAdd) {
        this.x += locationToAdd.getX();
        this.y += locationToAdd.getY();
        this.z += locationToAdd.getZ();
        this.yaw += locationToAdd.getYaw();
        this.pitch += locationToAdd.getPitch();
        return this;
    }

    /**
     * Adds the given vector's coordinates to this location
     *
     * @param vector Vector to add
     * @return This location object
     */
    public Location add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }

    /**
     * Subtracts the given coordinates from this location
     *
     * @param x X coordinate to subtract
     * @param y Y coordinate to subtract
     * @param z Z coordinate to subtract
     * @return This location object
     */
    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts the given coordinates and rotation from this location
     * @param x X coordinate to subtract
     * @param y Y coordinate to subtract
     * @param z Z coordinate to subtract
     * @param yaw Yaw rotation to subtract
     * @param pitch Pitch rotation to subtract
     * @return This location object
     */
    public Location subtract(double x, double y, double z,float yaw, float pitch) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.yaw -= yaw;
        this.pitch -= pitch;
        return this;
    }

    /**
     * Subtracts the given location's coordinates and rotation from this location
     *
     * @param locationToAdd Location to subtract
     * @return This location object
     */
    public Location subtract(Location locationToAdd) {
        this.x -= locationToAdd.getX();
        this.y -= locationToAdd.getY();
        this.z -= locationToAdd.getZ();
        this.yaw -= locationToAdd.getYaw();
        this.pitch -= locationToAdd.getPitch();
        return this;
    }

    /**
     * Subtracts the given vector's coordinates from this location
     *
     * @param vector Vector to subtract
     * @return This location object
     */
    public Location subtract(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
        this.z -= vector.getZ();
        return this;
    }

    /**
     * Resets this location's rotation to default values (0,0)
     *
     * @return This location object
     */
    public Location resetRotation() {
        this.yaw = 0;
        this.pitch = 0;
        return this;
    }

    /**
     * Converts this location's coordinates to a Bukkit's Vector object
     *
     * @return A Vector representing this location's coordinates
     */
    public Vector toVector() {
        return new Vector(x,y,z);
    }

    /**
     * Converts this location's coordinates to a Vector3d object
     * for PacketEvents systems
     *
     * @return A Vector3d representing this location's coordinates
     */
    public Vector3d toVector3d() {
        return new Vector3d(x,y,z);
    }

    /**
     * Gets the direction vector based on pitch/yaw
     *
     * @return A normalized vector pointing in the direction of this location's rotation
     */
    public Vector getDirection() {
        Vector vector = new Vector();
        double rotX = this.getYaw();
        double rotY = this.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    /**
     * Checks if another location is within a cubic radius
     *
     * @param location Location to check
     * @param distance Maximum distance on any axis
     * @return true if the location is within the distance
     */
    public boolean isNearby(Location location, double distance) {
        if (!location.getWorld().getUID().equals(getWorld().getUID())) return false;
        return Math.abs(location.getX() - x) <= distance && Math.abs(location.getY() - y) <= distance && Math.abs(location.getZ() - z) <= distance;
    }

    /**
     * Calculates the Euclidean distance to another location
     *
     * @param location Location to measure distance to
     * @return Distance to the location, or Double.MAX_VALUE if in different worlds
     */
    public double distance(Location location) {
        if (location == null) return Double.MAX_VALUE;
        if (!location.getWorld().getUID().equals(getWorld().getUID())) return Double.MAX_VALUE;
        return Math.sqrt(Math.pow(location.getX() - x, 2) + Math.pow(location.getY() - y, 2) + Math.pow(location.getZ() - z, 2));
    }

    /**
     * Creates a new location instance
     *
     * @param world World name
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return New Location object
     */
    public static Location of(String world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }
    public static Location of(String world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Converts a Bukkit location to this location type
     *
     * @param location Bukkit location to convert
     * @return New Location object
     */
    public static Location fromBukkit(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Converts a Player's location to this location type
     * @param player Player whose location to convert
     * @return New Location object
     */
    public static Location fromPlayer(Player player) {
        return fromBukkit(player.getLocation());
    }


    /**
     * Converts this location to a Bukkit Location object
     * @return Bukkit Location
     */
    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(getWorld(), x, y, z, yaw, pitch);
    }

    /**
     * Converts this location to a PacketEvents Location object
     * @return PacketEvents Location
     */
    public com.github.retrooper.packetevents.protocol.world.Location getProtocolLocation() {
        return new com.github.retrooper.packetevents.protocol.world.Location(x,y,z,yaw,pitch);
    }

    /**
     * Teleports an entity to this location
     * @param entity Entity to teleport
     */
    public void teleport(Entity entity) {
        entity.teleport(getBukkitLocation());
    }

    /**
     * Spawns an entity of the given type at this location
     * @param type Entity type to spawn
     * @return The spawned entity, or null if the world is not found
     */
    public Entity spawnEntity(EntityType type) {
        World world = getWorld();
        if (world == null) return null;
        return world.spawnEntity(getBukkitLocation(), type);
    }

    /**
     * Plays a sound at this location
     * @param sound Sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    public void playSound(Sound sound, float volume, float pitch) {
        World world = getWorld();
        if (world == null) return;
        world.playSound(getBukkitLocation(), sound, volume, pitch);
    }

    @Override
    public String toString() {
        return "Location{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private Location() {}
}