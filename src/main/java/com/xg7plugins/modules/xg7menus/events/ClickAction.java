package com.xg7plugins.modules.xg7menus.events;

public enum ClickAction {
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

    public boolean isShiftClick() {
        return this == SHIFT_LEFT || this == SHIFT_RIGHT || this == CONTROL_DROP;
    }
}
