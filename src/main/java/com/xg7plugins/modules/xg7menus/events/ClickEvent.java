package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import lombok.Getter;

@Getter
public class ClickEvent extends MenuEvent {

    private final int rawSlot;
    private final Slot slotClicked;
    private final ClickAction clickAction;


    public ClickEvent(MenuHolder holder, ClickAction clickAction, int rawSlot, Slot slotClicked) {
        super(holder);
        this.clickAction = clickAction;
        this.rawSlot = rawSlot;
        this.slotClicked = slotClicked;
    }
}
