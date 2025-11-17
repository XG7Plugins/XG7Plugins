package com.xg7plugins.modules.xg7menus.item.clickable;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import org.bukkit.inventory.ItemStack;

public abstract class ClickableItem extends InventoryItem {

    public ClickableItem(ItemStack itemStack, Slot slot) {
        super(itemStack, slot);
    }

    public abstract void onClick(ActionEvent event);

}
