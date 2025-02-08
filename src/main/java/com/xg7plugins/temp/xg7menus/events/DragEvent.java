package com.xg7plugins.temp.xg7menus.events;

import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.holders.MenuHolder;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.Set;

@Getter
public class DragEvent extends MenuEvent {

    private final List<Item> draggedItems;
    private final Set<Integer> draggedSlots;
    private final Set<Integer> draggedRawSlots;

    public DragEvent(HumanEntity whoClicked, MenuHolder menu, List<Item> draggedItems, Set<Integer> draggedSlots, Set<Integer> draggedRawSlots) {
        super(whoClicked, ClickAction.DRAG, menu, null);
        this.draggedItems = draggedItems;
        this.draggedSlots = draggedSlots;
        this.draggedRawSlots = draggedRawSlots;
    }
}
