package com.xg7plugins.libs.xg7menus.menus.gui;

import com.xg7plugins.libs.xg7menus.MenuException;
import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class ItemsPageMenu extends Menu {

    private static final Map<UUID, Integer> indexs = new HashMap<>();

    private List<BaseItemBuilder<?>> pageItems;
    private final Slot initSlot;
    private final Slot finalSlot;
    private final int area;
    private final boolean keepSavingPageIndex;

    public ItemsPageMenu(String id,String title, int size, Map<Integer, ItemStack> items, Map<Integer, Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player, Slot initSlot, Slot finalSlot, List<BaseItemBuilder<?>> pageItems, boolean keepSavingPageIndex) {
        super(id,title, size, items, clicks, defaultClick, openEvent, closeEvent, permissions, player);
        this.initSlot = initSlot;
        this.finalSlot = finalSlot;
        this.pageItems = pageItems;
        this.area = (finalSlot.getRow() - initSlot.getRow()) * (finalSlot.getColumn() - initSlot.getColumn());
        this.keepSavingPageIndex = keepSavingPageIndex;
    }

    public ItemsPageMenu(String id,String title, InventoryType type, Map<Integer, ItemStack> items, Map<Integer, Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player, Slot initSlot, Slot finalSlot, List<BaseItemBuilder<?>> pageItems, boolean keepSavingPageIndex) {
        super(id,title, type, items, clicks, defaultClick, openEvent, closeEvent, permissions, player);
        this.initSlot = initSlot;
        this.finalSlot = finalSlot;
        this.pageItems = pageItems;
        this.area = (finalSlot.getRow() - initSlot.getRow()) * (finalSlot.getColumn() - initSlot.getColumn());
        this.keepSavingPageIndex = keepSavingPageIndex;
    }

    public void updatePage(List<BaseItemBuilder<?>> pageItems) {
        this.pageItems = pageItems;
    }

    @Override
    public void open() {
        super.open();
        goPage(indexs.get(player.getUniqueId()) == null ? 0 : indexs.get(player.getUniqueId()));
    }
    @Override
    public void close() {
        super.close();
        if (!keepSavingPageIndex) indexs.remove(player.getUniqueId());
    }

    public void goPage(int index) {
        if (index * area > pageItems.size()) throw new MenuException("Index out of bounds");
        if (index < 0) throw new MenuException("Index out of bounds");

        indexs.put(player.getUniqueId(), index);

        List<BaseItemBuilder<?>> itemsToAdd = pageItems.subList(index * area, pageItems.size());


        int indexToAdd = 0;

        for (int x = initSlot.getRow(); x <= finalSlot.getRow(); x++) {
            for (int y = initSlot.getColumn(); y <= finalSlot.getColumn(); y++) {
                if (indexToAdd >= itemsToAdd.size()) {
                    if (inventory.getItem(Slot.get(x,y)) != null) setItem(Slot.get(x,y), new ItemStack(Material.AIR));
                    continue;
                }
                setItem(Slot.get(x,y), itemsToAdd.get(indexToAdd).toItemStack());
                if (itemsToAdd.get(indexToAdd).getEvent() != null) setClickEvent(Slot.get(x,y), itemsToAdd.get(indexToAdd).getEvent());
                indexToAdd++;
            }
        }
        update();
    }
    public void nextPage() {
        if ((indexs.get(player.getUniqueId()) + 1) * area > pageItems.size()) return;
        goPage(indexs.get(player.getUniqueId()) + 1);
    }
    public void previousPage() {
        if (indexs.get(player.getUniqueId()) - 1 < 0) return;
        goPage(indexs.get(player.getUniqueId()) - 1);
    }

}
