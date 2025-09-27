package com.xg7plugins.modules.xg7scores.organizer.impl;

import com.xg7plugins.modules.xg7scores.organizer.TabListRule;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

public class LuckpermsRule implements TabListRule {

    private final Group group;

    public LuckpermsRule(Group group) {
        this.group = group;
    }

    @Override
    public String getId() {
        return group.getName();
    }

    @Override
    public int priority() {
        return group.getWeight().orElse(Integer.MAX_VALUE);
    }

    @Override
    public boolean condition(Player player) {

        return player.hasPermission("group." + group.getName());
    }
}
