package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChangePageItem extends ClickableItem {

    private final Orientation orientation;

    public ChangePageItem(ItemStack itemStack, Orientation orientation) {
        super(itemStack);
        this.orientation = orientation;
    }

    public ChangePageItem(Orientation orientation) {
        super(new ItemStack(Material.ARROW));
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

    public static ChangePageItem nextPageItem() {
        return new ChangePageItem(Orientation.NEXT);
    }
    public static ChangePageItem previousPageItem() {
        return new ChangePageItem(Orientation.PREVIOUS);
    }

    public static ChangePageItem nextPageItem(ItemStack itemStack) {
        return new ChangePageItem(itemStack, Orientation.NEXT);
    }
    public static ChangePageItem previousPageItem(ItemStack itemStack) {
        return new ChangePageItem(itemStack, Orientation.PREVIOUS);
    }

}
