package com.xg7plugins.modules.xg7scores.organizer;

import org.bukkit.entity.Player;

public interface TabListRule {

    String getId();

    int priority();

    boolean condition(Player player);

}
