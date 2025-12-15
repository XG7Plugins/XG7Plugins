package com.xg7plugins.modules.xg7menus.item;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ClickableItemImpl;
import com.xg7plugins.utils.item.Item;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
public class InventoryItem extends Item {

    protected Slot slot;

    public InventoryItem(ItemStack itemStack, Slot slot) {
        super(itemStack);
        this.slot = slot;
    }

    public ClickableItem clickable(Consumer<ActionEvent> clickEvent) {
        return new ClickableItemImpl(this.itemStack, clickEvent, slot).setBuildPlaceholders(buildPlaceholders);
    }

}
