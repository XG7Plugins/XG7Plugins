package com.xg7plugins.modules.xg7scores.organizer.impl;

import com.xg7plugins.modules.xg7scores.organizer.TabListRule;
import org.bukkit.entity.Player;

public class NoPermRule implements TabListRule {
    @Override
    public String getId() {
        return "noperm";
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean condition(Player player) {
        return true;
    }
}
