package com.xg7plugins.temp.xg7scores;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ScoreCondition {

    boolean verify(Player player);

}
