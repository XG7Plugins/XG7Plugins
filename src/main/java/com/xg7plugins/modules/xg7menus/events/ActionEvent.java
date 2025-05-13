package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import lombok.Getter;

@Getter
public class ActionEvent extends MenuEvent {

    private final int rawSlot;
    private final Slot clickedSlot;
    private final MenuAction menuAction;
    private final Item clickedItem;


    public ActionEvent(BasicMenuHolder holder, MenuAction menuAction, int rawSlot, Slot clickedSlot, Item clickedItem) {
        super(holder);
        this.menuAction = menuAction;
        this.rawSlot = rawSlot;
        this.clickedSlot = clickedSlot;
        this.clickedItem = clickedItem;
    }
}
