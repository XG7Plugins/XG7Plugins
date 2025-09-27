package com.xg7plugins.modules.xg7scores.organizer.impl;

import com.xg7plugins.modules.xg7scores.organizer.TabListRule;
import org.bukkit.entity.Player;

public class OPRule implements TabListRule {
    @Override
    public String getId() {
        return "1_op";
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean condition(Player player) {
        return player.isOp();
    }
}
