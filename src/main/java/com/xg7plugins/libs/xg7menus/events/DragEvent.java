package com.xg7plugins.libs.xg7menus.events;

import com.xg7plugins.libs.xg7menus.BaseMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

@Getter
public class DragEvent extends ClickEvent {

    private final Map<Integer,ItemStack> clickedItems;
    private final Set<Integer> clickedSlots;

    public DragEvent(Player whoClicked, Set<Integer> clickedSlots, Map<Integer, ItemStack> clickedItems, BaseMenu clickedMenu) {
        super(whoClicked, ClickAction.DRAG , 0, null, clickedMenu, null);
        this.clickedSlots = clickedSlots;
        this.clickedItems = clickedItems;
    }
}
