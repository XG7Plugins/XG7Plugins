package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import lombok.Getter;

@Getter
public class ActionEvent extends MenuEvent {

    private final int rawSlot;
    private final Slot slotClicked;
    private final MenuAction menuAction;
    private final Item itemClicked;


    public ActionEvent(BasicMenuHolder holder, MenuAction menuAction, int rawSlot, Slot slotClicked, Item itemClicked) {
        super(holder);
        this.menuAction = menuAction;
        this.rawSlot = rawSlot;
        this.slotClicked = slotClicked;
        this.itemClicked = itemClicked;
    }
}
