package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class Location {

    private String world;
    private double x;
    private double y;
    private double z;

    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    public Location add(double x, double y, double z) {
        return new Location(world, this.x + x, this.y + y, this.z + z);
    }
    public static Location fromBukkit(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }
    public static Location fromPlayer(Player player) {
        return fromBukkit(player.getLocation());
    }

    @Override
    public String toString() {
        return "Location{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
