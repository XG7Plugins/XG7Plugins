package com.xg7plugins.modules.xg7menus.editor;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor
public class InventoryEditor implements InventoryShaper {

    private final MenuConfigurations menuConfigurations;
    private final HashMap<Slot, Item> items = new HashMap<>();

    @Override
    public void setItem(Slot slot, Item item) {
        if (item == null) {
            items.remove(slot);
        } else {
            items.put(slot, item);
        }
    }

    @Override
    public void addItem(Item item) {
        if (item == null) return;
        if (item.getSlot() >= 0) {
            setItem(Slot.fromSlot(item.getSlot()), item);
            return;
        }
        items.put(Slot.fromSlot(items.size()), item);
    }

    @Override
    public Item getItem(Slot slot) {
        return items.get(slot);
    }

    @Override
    public void removeItem(Slot slot) {
        items.remove(slot);
    }

    @Override
    public boolean hasItem(Slot slot) {
        return items.containsKey(slot);
    }

    @Override
    public void clearInventory() {
        items.clear();
    }

    @Override
    public void fillInventory(Item item) {
        IntStream.range(0, menuConfigurations.getRows() * 9).forEach(i -> items.put(Slot.fromSlot(i), item));
    }

    @Override
    public void fillInventory(List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = 0; i < menuConfigurations.getRows() * 9; i++) {
            if (index >= itemList.size()) {
                if (!fillAll) break;
                index = 0;
                if (randomize) Collections.shuffle(itemList);
            }
            setItem(Slot.fromSlot(i), itemList.get(index));
            index++;
        }
    }

    @Override
    public void fillRow(int row, Item item) {
        IntStream.range(0, 9).forEach(i -> items.put(Slot.of(row, i + 1), item));
    }

    @Override
    public void fillRow(int row, List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = 0; i < 9; i++) {
            if (index >= itemList.size()) {
                if (!fillAll) break;
                index = 0;
                if (randomize) Collections.shuffle(itemList);
            }
            setItem(Slot.of(row, i + 1), itemList.get(index));
            index++;
        }
    }

    @Override
    public void fillCol(int col, Item item) {
        IntStream.range(0, menuConfigurations.getRows()).forEach(i -> items.put(Slot.of(i + 1, col), item));
    }

    @Override
    public void fillCol(int col, List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = 0; i < menuConfigurations.getRows(); i++) {
            if (index >= itemList.size()) {
                if (!fillAll) break;
                index = 0;
                if (randomize) Collections.shuffle(itemList);
            }
            setItem(Slot.of(i + 1, col), itemList.get(index));
            index++;
        }
    }

    @Override
    public void fillRectangle(Slot startSlot, Slot endSlot, Item item) {
        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                setItem(Slot.of(i, j), item);
            }
        }
    }

    @Override
    public void fillRectangle(Slot startSlot, Slot endSlot, List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                if (index >= itemList.size()) {
                    if (!fillAll) break;
                    index = 0;
                    if (randomize) Collections.shuffle(itemList);
                }
                setItem(Slot.of(i, j), itemList.get(index));
                index++;
            }
        }

    }

    @Override
    public void frame(Slot startSlot, Slot endSlot, Item item) {
        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                if (i != startSlot.getRow() || i != endSlot.getRow() || j != startSlot.getColumn() || j != endSlot.getColumn()) continue;
                setItem(Slot.of(i, j), item);
            }
        }
    }

    public void frame(Slot startSlot, Slot endSlot, List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                if (index >= itemList.size()) {
                    if (!fillAll) break;
                    index = 0;
                    if (randomize) Collections.shuffle(itemList);
                }
                if (i != startSlot.getRow() || i != endSlot.getRow() || j != startSlot.getColumn() || j != endSlot.getColumn()) continue;
                setItem(Slot.of(i, j), itemList.get(index));
                index++;
            }
        }
    }

    @Override
    public List<Item> getItems() {
        return Collections.emptyList();
    }
}
