package com.xg7plugins.modules.xg7menus.item.clickable;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.inventory.ItemStack;

public abstract class ClickableItem extends Item {

    public ClickableItem(ItemStack itemStack) {
        super(itemStack);
    }



    public abstract void onClick(ActionEvent event);

}
