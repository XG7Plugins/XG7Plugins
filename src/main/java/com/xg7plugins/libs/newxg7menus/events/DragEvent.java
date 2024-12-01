package com.xg7plugins.libs.newxg7menus.events;

import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
import com.xg7plugins.libs.newxg7menus.item.Item;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;

import java.util.Map;
import java.util.Set;

@Getter
public class DragEvent extends MenuEvent {

    private final Map<Integer, Item> clickedItems;
    private final Set<Integer> clickedSlots;

    public DragEvent(HumanEntity whoClicked, Map<Integer, Item> clickedItems, Set<Integer> clickedSlots) {
        super(whoClicked, ClickAction.DRAG, null);
        this.clickedItems = clickedItems;
        this.clickedSlots = clickedSlots;
    }
}
