package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters;

import org.bukkit.entity.Player;


public interface SidebarUpdater {

    boolean checkVersion(Player player);

    void setLine(Player player, int score, String text);

    void removeLine(Player player, int score);

    default void prepareToRemove(Player player) {

    }


}
