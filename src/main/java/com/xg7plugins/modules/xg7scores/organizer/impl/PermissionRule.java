package com.xg7plugins.modules.xg7scores.organizer.impl;

import com.xg7plugins.modules.xg7scores.organizer.TabListRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PermissionRule implements TabListRule {

    private int priority;
    private String permission;

    @Override
    public String getId() {
        return permission;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public boolean condition(Player player) {
        return player.hasPermission(permission);
    }
}
