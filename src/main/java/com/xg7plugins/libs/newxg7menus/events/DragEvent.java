package com.xg7plugins.libs.newxg7menus.events;

import com.xg7plugins.libs.newxg7menus.Menu;
import com.xg7plugins.libs.newxg7menus.item.Item;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

@Getter
public class DragEvent extends MenuEvent {

    private final Map<Integer, Item> clickedItems;
    private final Set<Integer> clickedSlots;

    public DragEvent(HumanEntity whoClicked, Menu menu, Map<Integer, Item> clickedItems, Set<Integer> clickedSlots) {
        super(whoClicked, ClickAction.DRAG, menu, null);
        this.clickedItems = clickedItems;
        this.clickedSlots = clickedSlots;
    }
}
