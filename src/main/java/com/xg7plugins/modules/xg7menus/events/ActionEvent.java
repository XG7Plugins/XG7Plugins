package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import lombok.Getter;

@Getter
public class ActionEvent extends MenuEvent {

    private final int rawSlot;
    private final Slot clickedSlot;
    private final MenuAction menuAction;
    private final InventoryItem clickedItem;


    public ActionEvent(BasicMenuHolder holder, MenuAction menuAction, int rawSlot, Slot clickedSlot, InventoryItem clickedItem, boolean cancelled) {
        super(holder, cancelled);
        this.menuAction = menuAction;
        this.rawSlot = rawSlot;
        this.clickedSlot = clickedSlot;
        this.clickedItem = clickedItem;
    }
}
