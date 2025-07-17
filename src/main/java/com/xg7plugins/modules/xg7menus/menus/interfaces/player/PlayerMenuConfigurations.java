package com.xg7plugins.modules.xg7menus.menus.interfaces.player;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.BasicMenuConfigs;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.utils.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

    static PlayerMenuConfigurations of(Plugin plugin, String id,
                                           EnumSet<MenuAction> allowedActions, boolean enabled,
                                           List<Pair<String,String>> placeholders,
                                           String onDropMessage, String onPickupMessage,
                                           String onBreakMessage, String onPlaceMessage,
                                           String onClickMessage, String onDragMessage,
                                           String onInteractMessage) {
        return new PlayerMenuConfigsImpl(plugin, id, allowedActions, enabled, placeholders,
                onDropMessage, onPickupMessage, onBreakMessage, onPlaceMessage,
                onClickMessage, onDragMessage, onInteractMessage);
    }

    static PlayerMenuConfigurations of(Plugin plugin, String id) {
        return of(plugin, id, null);
    }
    static PlayerMenuConfigurations of(Plugin plugin, String id, boolean enabled) {
        return of(plugin, id, null, enabled);
    }
    static PlayerMenuConfigurations of(Plugin plugin, String id, EnumSet<MenuAction> allowedActions) {
        return of(plugin, id, allowedActions, true, new ArrayList<>());
    }

    static PlayerMenuConfigurations of(Plugin plugin, String id, EnumSet<MenuAction> allowedActions, boolean enabled) {
        return of(plugin, id, allowedActions, enabled, new ArrayList<>());
    }
    static PlayerMenuConfigurations of(Plugin plugin, String id,
                                           EnumSet<MenuAction> allowedActions, boolean enabled,
                                           List<Pair<String,String>> placeholders) {
        return of(plugin, id,allowedActions,enabled,placeholders,"","","","","","","");
    }

    static PlayerMenuConfigurations ofWithMessages(Plugin plugin, String id,
                                                       String onDropMessage, String onPickupMessage,
                                                       String onBreakMessage, String onPlaceMessage,
                                                       String onClickMessage, String onDragMessage,
                                                       String onInteractMessage) {
        return of(plugin, id, null, true, new ArrayList<>(),
                onDropMessage, onPickupMessage, onBreakMessage, onPlaceMessage,
                onClickMessage, onDragMessage, onInteractMessage);
    }


}
