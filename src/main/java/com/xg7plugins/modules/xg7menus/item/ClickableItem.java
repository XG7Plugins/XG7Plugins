package com.xg7plugins.modules.xg7menus.item;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
public class ClickableItem extends Item {

    private Consumer<ActionEvent> onClick;

    public ClickableItem(ItemStack itemStack, int slot) {
        super(itemStack);
        this.slot(slot);
    }

    public ClickableItem onClick(Consumer<ActionEvent> onClick) {
        this.onClick = onClick;
        return this;
    }


}
