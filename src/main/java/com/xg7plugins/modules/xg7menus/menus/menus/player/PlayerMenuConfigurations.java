package com.xg7plugins.modules.xg7menus.menus.menus.player;

import com.xg7plugins.modules.xg7menus.menus.BasicMenuConfigs;
import org.bukkit.entity.Player;

public interface PlayerMenuConfigurations extends BasicMenuConfigs {

    default String getOnDropMessage(Player player) {
        return "";
    }
    default String getOnPickupMessage(Player player) {
        return "";
    }
    default String getOnBreakMessage(Player player) {
        return "";
    }
    default String getOnPlaceMessage(Player player) {
        return "";
    }
    default String getOnClickMessage(Player player) {
        return "";
    }
    default String getOnDragMessage(Player player) {
        return "";
    }
    default String getOnInteractMessage(Player player) {
        return "";
    }


}
