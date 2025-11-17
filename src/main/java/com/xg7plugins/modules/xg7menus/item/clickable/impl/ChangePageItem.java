package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChangePageItem extends ClickableItem {

    private final Orientation orientation;

    public ChangePageItem(Slot slot, ItemStack itemStack, Orientation orientation) {
        super(itemStack, slot);
        this.orientation = orientation;
    }

    public ChangePageItem(Slot slot, Orientation orientation) {
        super(new ItemStack(Material.ARROW), slot);
        this.orientation = orientation;
    }

    @Override
    public void onClick(ActionEvent event) {

        if (!(event.getHolder() instanceof PagedMenuHolder)) return;

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        switch (orientation) {
            case NEXT:
                holder.nextPage();
                break;
            case PREVIOUS:
                holder.previousPage();
                break;
        }

    }

    public enum Orientation {
        NEXT, PREVIOUS
    }

    public static ChangePageItem nextPageItem(Slot slot) {
        return new ChangePageItem(slot, Orientation.NEXT);
    }
    public static ChangePageItem previousPageItem(Slot slot) {
        return new ChangePageItem(slot, Orientation.PREVIOUS);
    }

    public static ChangePageItem nextPageItem(Slot slot, ItemStack itemStack) {
        return new ChangePageItem(slot, itemStack, Orientation.NEXT);
    }
    public static ChangePageItem previousPageItem(Slot slot, ItemStack itemStack) {
        return new ChangePageItem(slot, itemStack, Orientation.PREVIOUS);
    }

}
