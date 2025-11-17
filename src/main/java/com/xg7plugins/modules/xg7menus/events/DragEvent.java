package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class DragEvent extends MenuEvent {

    private final List<InventoryItem> draggedItems;
    private final Set<Slot> draggedSlots;
    private final Set<Integer> draggedRawSlots;
    private final MenuAction menuAction = MenuAction.DRAG;

    public DragEvent(BasicMenuHolder holder, List<InventoryItem> draggedItems, Set<Slot> draggedSlots, Set<Integer> draggedRawSlots, boolean cancelled) {
        super(holder, cancelled);

        this.draggedItems = draggedItems;
        this.draggedSlots = draggedSlots;
        this.draggedRawSlots = draggedRawSlots;
    }
}
