package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;

public class CloseInventoryItem extends ClickableItem {

    private final Menu backMenu;

    public CloseInventoryItem() {
        this(null);
    }
    public CloseInventoryItem(Menu backMenu) {
        super(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR).parseItem());
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

    public static CloseInventoryItem get() {
        return new CloseInventoryItem();
    }

    public static CloseInventoryItem get(Menu backMenu) {
        return new CloseInventoryItem(backMenu);
    }
}
