package com.xg7plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Represents a playable sound with configurable properties that can be played
 * in different contexts within the Minecraft world.
 */
@Data
@AllArgsConstructor
public class PlayableSound {

    /**
     * The type of sound to be played
     */
    private Sound sound;
    /**
     * The volume of the sound (0.0 to 1.0)
     */
    private float volume;
    /**
     * The pitch of the sound (0.5 to 2.0)
     */
    private float pitch;

    /**
     * Plays the sound at a specific location in a world
     *
     * @param world    The world where the sound will be played
     * @param location The location where the sound will be played
     */
    public void play(World world, Location location) {
        world.playSound(location, sound, volume, pitch);
    }

    /**
     * Plays the sound at a specific location using a custom Location object
     *
     * @param location The custom location where the sound will be played
     */
    public void play(com.xg7plugins.utils.location.Location location) {
        play(location.getWorld(), location.getBukkitLocation());
    }

    /**
     * Plays the sound at a player's current location
     *
     * @param player The player at whose location the sound will be played
     */
    public void play(Player player) {
        play(player.getWorld(), player.getLocation());
    }

}
