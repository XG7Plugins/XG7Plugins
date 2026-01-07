package com.xg7plugins.modules.xg7menus.editor;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.utils.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class InventoryUpdater implements InventoryEditor {

    private final BasicMenuHolder holder;

    private final HashMap<Slot, InventoryItem> items = new HashMap<>();

    public InventoryUpdater(BasicMenuHolder holder) {
        this.holder = holder;

        for (InventoryItem item : holder.getMenu().getItems(holder.getPlayer())) setItem(item);
    }

    @Override
    public void setItem(Slot slot, Item item) {
        if (item.isAir()) {
            holder.getInventory().clear(slot.get());
            holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_REMOVED);
            return;
        }
        holder.getInventory().setItem(slot.get(), item.getItemFor(holder.getPlayer(), holder.getMenu().getMenuConfigs().getPlugin()));
        items.put(slot, item instanceof InventoryItem ? (InventoryItem) item : item.toInventoryItem(slot));

        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_CHANGED);
    }

    @Override
    public void setItem(InventoryItem item) {
        if (item == null) return;
        items.put(item.getSlot(), item);

        holder.getInventory().setItem(item.getSlot().get(), item.getItemFor(holder.getPlayer(), holder.getMenu().getMenuConfigs().getPlugin()));

        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_CHANGED);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) return;

        int slot = holder.getInventory().addItem(item.getItemFor(holder.getPlayer(), holder.getMenu().getMenuConfigs().getPlugin())).keySet().iterator().next();

        items.put(Slot.fromSlot(slot), item.toInventoryItem(slot));
        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_CHANGED);

    }

    public void refresh() {
        items.forEach(this::setItem);
    }


    @Override
    @NotNull
    public InventoryItem getItem(Slot slot) {
        return items.get(slot) == null ? Item.air().toInventoryItem(slot) : items.get(slot);
    }

    @Override
    public boolean hasItem(Slot slot) {
        return items.containsKey(slot) && !items.get(slot).isAir();
    }

    @Override
    public void removeItem(Slot slot) {
        holder.getInventory().clear(slot.get());
        items.remove(slot);
        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_REMOVED);
    }

    @Override
    public void clearInventory() {
        holder.getInventory().clear();
        items.clear();
        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_CLEARED);
    }

    public void fillInventory(Item item, boolean override) {
        if (override) {
            IntStream.range(0, holder.getInventory().getSize()).forEach(i -> setItem(Slot.fromSlot(i), item));
            holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
            return;
        }
        for (int i = 0; i < holder.getInventory().getSize(); i++) {
            if (hasItem(Slot.fromSlot(i))) continue;
            setItem(Slot.fromSlot(i), item);
        }
        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);

    }
    @Override
    public void fillInventory(Item item) {
        fillInventory(item, true);
    }

    public void fillInventory(List<Item> items, boolean randomize, boolean fillAll, boolean override) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = 0; i < holder.getInventory().getSize(); i++) {
            if (index >= itemList.size()) {
                if (!fillAll) break;
                index = 0;
                if (randomize) Collections.shuffle(itemList);
            }

            index++;

            if (!override && hasItem(Slot.fromSlot(i))) continue;

            setItem(Slot.fromSlot(i), itemList.get(index));

        }

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public void fillInventory(List<Item> items, boolean randomize, boolean fillAll) {
        fillInventory(items, randomize, fillAll, true);
    }
    @Override
    public void fillRow(int row, Item item) {
        IntStream.range(0, 9).forEach(i -> setItem(Slot.of(row, i + 1), item));
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

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public void fillCol(int col, Item item) {
        IntStream.range(0, holder.getInventory().getSize() / 9).forEach(i -> setItem(Slot.of(i + 1, col), item));
        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public void fillCol(int col, List<Item> items, boolean randomize, boolean fillAll) {
        if (items.isEmpty()) return;
        List<Item> itemList = new ArrayList<>(items);
        int index = 0;
        if (randomize) Collections.shuffle(itemList);

        for (int i = 0; i < holder.getInventory().getSize() / 9; i++) {
            if (index >= itemList.size()) {
                if (!fillAll) break;
                index = 0;
                if (randomize) Collections.shuffle(itemList);
            }
            setItem(Slot.of(i + 1, col), itemList.get(index));
            index++;
        }

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public void fillRectangle(Slot startSlot, Slot endSlot, Item item) {
        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                setItem(Slot.of(i, j), item);
            }
        }

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
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

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);

    }

    @Override
    public void frame(Slot startSlot, Slot endSlot, Item item) {
        for (int i = startSlot.getRow(); i <= endSlot.getRow(); i++) {
            for (int j = startSlot.getColumn(); j <= endSlot.getColumn(); j++) {
                if (i != startSlot.getRow() || i != endSlot.getRow() || j != startSlot.getColumn() || j != endSlot.getColumn()) continue;
                setItem(Slot.of(i, j), item);
            }
        }

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
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

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public List<InventoryItem> getItems() {
        return new ArrayList<>(items.values());
    }

}
