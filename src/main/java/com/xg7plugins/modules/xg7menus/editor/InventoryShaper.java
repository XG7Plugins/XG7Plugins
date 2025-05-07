package com.xg7plugins.modules.xg7menus.editor;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;

import java.util.List;

public interface InventoryShaper {

    void setItem(Slot slot, Item item);

    Item getItem(Slot slot);

    void removeItem(Slot slot);

    void clearInventory();

    void fillInventory(Item item);
    void fillInventory(List<Item> items, boolean randomize, boolean fillAll);

    void fillRow(int row, Item item);
    void fillRow(int row, List<Item> items, boolean randomize, boolean fillAll);

    void fillCol(int col, Item item);
    void fillCol(int col, List<Item> items, boolean randomize, boolean fillAll);

    void fillRectangle(Slot startSlot, Slot endSlot, Item item);
    void fillRectangle(Slot startSlot, Slot endSlot, List<Item> items, boolean randomize, boolean fillAll);

    void frame(Slot startSlot, Slot endSlot, Item item);

    void frame(Slot startSlot, Slot endSlot, List<Item> items, boolean randomize, boolean fillAll);

    List<Item> getItems();


}
