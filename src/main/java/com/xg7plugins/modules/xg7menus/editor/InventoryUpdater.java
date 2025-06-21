package com.xg7plugins.modules.xg7menus.editor;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@AllArgsConstructor
public class InventoryUpdater implements InventoryEditor {

    private final BasicMenuHolder holder;

    @Getter
    private final HashMap<Integer, Consumer<ActionEvent>> clickActions = new HashMap<>();

    @Override
    public void setItem(Slot slot, Item item) {
        if (item.isAir()) {
            holder.getInventory().clear(slot.get());
            clickActions.remove(slot.get());
            holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_REMOVED);
            return;
        }
        holder.getInventory().setItem(slot.get(), item.getItemFor(holder.getPlayer(), holder.getMenu().getMenuConfigs().getPlugin()));

        if (item instanceof ClickableItem && item.getSlot() == slot.get()) {
            ClickableItem clickableItem = (ClickableItem) item;
            clickActions.put(slot.get(), clickableItem.getOnClick());
            holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_CHANGED);
            return;
        }

        clickActions.remove(slot.get());

        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_CHANGED);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) return;
        if (item.getSlot() >= 0) {
            setItem(Slot.fromSlot(item.getSlot()), item);
            return;
        }
        holder.getInventory().addItem(item.getItemFor(holder.getPlayer(), holder.getMenu().getMenuConfigs().getPlugin()));
    }


    @Override
    public Item getItem(Slot slot) {
        return Item.from(holder.getInventory().getItem(slot.get())).slot(slot);
    }

    @Override
    public boolean hasItem(Slot slot) {
        return !Item.from(holder.getInventory().getItem(slot.get())).isAir();
    }

    @Override
    public void removeItem(Slot slot) {
        holder.getInventory().clear(slot.get());
        clickActions.remove(slot.get());
        holder.getMenu().onUpdate(holder, MenuUpdateActions.ITEM_REMOVED);
    }

    @Override
    public void clearInventory() {
        holder.getInventory().clear();
        clickActions.clear();
        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_CLEARED);
    }

    @Override
    public void fillInventory(Item item) {
        IntStream.range(0, holder.getInventory().getSize()).forEach(i -> setItem(Slot.fromSlot(i), item));

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
    }

    @Override
    public void fillInventory(List<Item> items, boolean randomize, boolean fillAll) {
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

            setItem(Slot.fromSlot(i), itemList.get(index));

            index++;
        }

        holder.getMenu().onUpdate(holder, MenuUpdateActions.INV_FILLED);
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
    public List<Item> getItems() {
        return Collections.emptyList();
    }

    public Consumer<ActionEvent> getClickAction(Slot slot) {
        return clickActions.get(slot.get());
    }

    public void setClickAction(Slot slot, Consumer<ActionEvent> action) {
        clickActions.put(slot.get(), action);
    }

    public boolean hasClickActionOn(Slot slot) {
        return clickActions.containsKey(slot.get());
    }

}
