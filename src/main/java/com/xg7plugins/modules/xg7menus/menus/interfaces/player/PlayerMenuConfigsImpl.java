package com.xg7plugins.modules.xg7menus.menus.interfaces.player;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
public class PlayerMenuConfigsImpl implements PlayerMenuConfigurations {

    private final Plugin plugin;
    private final String id;
    private final EnumSet<MenuAction> allowedActions;
    private final boolean enabled;
    private final List<Pair<String,String>> placeholders;

    private final String onDropMessage;
    private final String onPickupMessage;
    private final String onBreakMessage;
    private final String onPlaceMessage;
    private final String onClickMessage;
    private final String onDragMessage;
    private final String onInteractMessage;

    private final long repeatingUpdateDelay;

    @Override
    public long repeatingUpdateMills() {
        return repeatingUpdateDelay;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public EnumSet<MenuAction> allowedActions() {
        return allowedActions;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public List<Pair<String, String>> getPlaceholders() {
        return placeholders;
    }

    @Override
    public String getOnDropMessage(Player player) {
        return onDropMessage;
    }

    @Override
    public String getOnPickupMessage(Player player) {
        return onPickupMessage;
    }

    @Override
    public String getOnBreakMessage(Player player) {
        return onBreakMessage;
    }

    @Override
    public String getOnPlaceMessage(Player player) {
        return onPlaceMessage;
    }

    @Override
    public String getOnClickMessage(Player player) {
        return onClickMessage;
    }

    @Override
    public String getOnDragMessage(Player player) {
        return onDragMessage;
    }

    @Override
    public String getOnInteractMessage(Player player) {
        return onInteractMessage;
    }
}
