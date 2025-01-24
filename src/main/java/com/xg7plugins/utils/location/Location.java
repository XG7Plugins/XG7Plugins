package com.xg7plugins.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@AllArgsConstructor
@Getter
public class Location implements Cloneable {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    public String getWorldName() {
        return world;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    public Location add(double x, double y, double z,float yaw, float pitch) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.yaw += yaw;
        this.pitch += pitch;
        return this;
    }
    public Location add(Location locationToAdd) {
        this.x += locationToAdd.getX();
        this.y += locationToAdd.getY();
        this.z += locationToAdd.getZ();
        this.yaw += locationToAdd.getYaw();
        this.pitch += locationToAdd.getPitch();
        return this;
    }
    public Location add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }
    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    public Location subtract(double x, double y, double z,float yaw, float pitch) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.yaw -= yaw;
        this.pitch -= pitch;
        return this;
    }
    public Location subtract(Location locationToAdd) {
        this.x -= locationToAdd.getX();
        this.y -= locationToAdd.getY();
        this.z -= locationToAdd.getZ();
        this.yaw -= locationToAdd.getYaw();
        this.pitch -= locationToAdd.getPitch();
        return this;
    }
    public Location subtract(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
        this.z -= vector.getZ();
        return this;
    }

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

    public boolean isNearby(Location location, double distance) {
        return Math.abs(location.getX() - x) <= distance && Math.abs(location.getY() - y) <= distance && Math.abs(location.getZ() - z) <= distance;
    }

    public static Location of(String world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }
    public static Location of(String world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }
    public static Location fromBukkit(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    public static Location fromPlayer(Player player) {
        return fromBukkit(player.getLocation());
    }
    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(getWorld(), x, y, z, yaw, pitch);
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