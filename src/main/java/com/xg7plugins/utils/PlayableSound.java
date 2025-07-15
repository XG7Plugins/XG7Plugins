package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class PlayableSound {

    private Sound sound;
    private float volume;
    private float pitch;

    public void play(World world, Location location) {
        world.playSound(location, sound, volume, pitch);
    }

    public void play(com.xg7plugins.utils.location.Location location) {
        play(location.getWorld(), location.getBukkitLocation());
    }

    public void play(Player player) {
        play(player.getWorld(), player.getLocation());
    }

}
