package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItemImpl extends ClickableItem {

    private final Consumer<ActionEvent> onClick;

    public ClickableItemImpl(ItemStack itemStack, Consumer<ActionEvent> onClick, Slot slot) {
        super(itemStack, slot);
        this.onClick = onClick;
    }

    @Override
    public void onClick(ActionEvent event) {
        onClick.accept(event);
    }
}
