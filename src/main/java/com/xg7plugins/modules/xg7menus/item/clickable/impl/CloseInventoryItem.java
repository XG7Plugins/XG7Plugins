package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;

public class CloseInventoryItem extends ClickableItem {

    private final Menu backMenu;

    public CloseInventoryItem(Slot slot) {
        this(slot, null);
    }
    public CloseInventoryItem(Slot slot, Menu backMenu) {
        super(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR).parseItem(), slot);
        this.backMenu = backMenu;
    }

    @Override
    public void onClick(ActionEvent event) {
        if (backMenu != null) {
            backMenu.open(event.getHolder().getPlayer());
            return;
        }
        event.getHolder().getPlayer().closeInventory();
    }

    public static CloseInventoryItem get(Slot slot) {
        return new CloseInventoryItem(slot);
    }

    public static CloseInventoryItem get(Slot slot, Menu backMenu) {
        return new CloseInventoryItem(slot, backMenu);
    }
}
