package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class DragEvent extends MenuEvent {

    private final List<Item> draggedItems;
    private final Set<Slot> draggedSlots;
    private final Set<Integer> draggedRawSlots;
    private final MenuAction menuAction = MenuAction.DRAG;

    public DragEvent(BasicMenuHolder holder, List<Item> draggedItems, Set<Slot> draggedSlots, Set<Integer> draggedRawSlots) {
        super(holder);

        this.draggedItems = draggedItems;
        this.draggedSlots = draggedSlots;
        this.draggedRawSlots = draggedRawSlots;
    }
}
