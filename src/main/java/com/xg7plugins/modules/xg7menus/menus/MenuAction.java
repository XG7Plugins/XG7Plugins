package com.xg7plugins.modules.xg7menus.menus;

import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;

public enum MenuAction {
    LEFT,
    SHIFT_LEFT,
    RIGHT,
    SHIFT_RIGHT,
    WINDOW_BORDER_LEFT,
    WINDOW_BORDER_RIGHT,
    MIDDLE,
    NUMBER_KEY,
    KEYBOARD,
    DOUBLE_CLICK,
    DROP,
    CONTROL_DROP,
    CREATIVE,
    SWAP_OFFHAND,
    UNKNOWN,

    DRAG,

    PLAYER_DROP,
    PLAYER_PICKUP,
    PLAYER_BREAK_BLOCK,
    PLAYER_PLACE_BLOCK,

    LEFT_CLICK_BLOCK,
    RIGHT_CLICK_BLOCK,
    LEFT_CLICK_AIR,
    RIGHT_CLICK_AIR,
    PHYSICAL;
    public boolean isKeyboardClick() {
        return this == NUMBER_KEY || this == DROP || this == CONTROL_DROP || this == KEYBOARD;
    }

    public boolean isCreativeAction() {
        return this == MIDDLE || this == CREATIVE;
    }

    public boolean isRightClick() {
        return this == RIGHT || this == SHIFT_RIGHT || this == RIGHT_CLICK_AIR || this == RIGHT_CLICK_BLOCK;
    }

    public boolean isLeftClick() {
        return this == LEFT || this == SHIFT_LEFT || this == DOUBLE_CLICK || this == CREATIVE || this == LEFT_CLICK_AIR || this == LEFT_CLICK_BLOCK;
    }

    public boolean isAirInteract() {
        return this == LEFT_CLICK_AIR || this == RIGHT_CLICK_AIR;
    }

    public boolean isPlayerInteract() {
        return this == PLAYER_DROP || this == PLAYER_PICKUP || this == PLAYER_BREAK_BLOCK || this == PLAYER_PLACE_BLOCK || this == LEFT_CLICK_AIR || this == RIGHT_CLICK_AIR || this == PHYSICAL || this == LEFT_CLICK_BLOCK || this == RIGHT_CLICK_BLOCK;
    }

    public boolean isMenuInteract() {
        return this == LEFT || this == SHIFT_LEFT || this == RIGHT || this == SHIFT_RIGHT || this == WINDOW_BORDER_LEFT || this == WINDOW_BORDER_RIGHT || this == MIDDLE || this == NUMBER_KEY || this == KEYBOARD || this == DOUBLE_CLICK || this == DROP || this == CONTROL_DROP || this == CREATIVE || this == SWAP_OFFHAND;
    }


    public boolean isBlockInteract() {
        return this == RIGHT_CLICK_BLOCK || this == LEFT_CLICK_BLOCK;
    }

    public boolean isShiftClick() {
        return this == SHIFT_LEFT || this == SHIFT_RIGHT || this == CONTROL_DROP;
    }

    public static MenuAction from(ClickType clickType) {
        return MenuAction.valueOf(clickType.name());
    }

    public static MenuAction from(Action action) {
        return MenuAction.valueOf(action.name());
    }

    public static MenuAction from(String name) {
        try {
            return MenuAction.valueOf(name);
        } catch (IllegalArgumentException e) {
            return MenuAction.UNKNOWN;
        }
    }
}
