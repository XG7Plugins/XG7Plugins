package com.xg7plugins.libs.xg7menus.menus.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StorageMenu extends Menu {

    private final Map<Integer, ItemStack> storageItems;
    private final Slot initStorageSlot;
    private final Slot finalStorageSlot;

    public StorageMenu(String id,String title, int size, Map<Integer, ItemStack> items, Map<Integer, Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player, Slot initStorageSlot, Slot finalStorageSlot, Map<Integer, ItemStack> storageItems) {
        super(id,title, size, items, clicks, defaultClick, openEvent, closeEvent,permissions, player);
        this.initStorageSlot = initStorageSlot;
        this.finalStorageSlot = finalStorageSlot;
        this.storageItems = storageItems;

        storageItems.forEach((key, value) -> inventory.setItem(key, value));

    }

    public StorageMenu(String id,String title, InventoryType type, Map<Integer, ItemStack> items, Map<Integer, Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player, Slot initStorageSlot, Slot finalStorageSlot, Map<Integer, ItemStack> storageItems) {
        super(id,title, type, items, clicks, defaultClick, openEvent, closeEvent,permissions, player);
        this.initStorageSlot = initStorageSlot;
        this.finalStorageSlot = finalStorageSlot;
        this.storageItems = storageItems;

        storageItems.forEach((key, value) -> inventory.setItem(key, value));

    }

    public StorageMenu store() {

        storageItems.clear();

        for (int y = initStorageSlot.getColumn(); y <= finalStorageSlot.getColumn(); y++) {
            for (int x = initStorageSlot.getRow(); x <= finalStorageSlot.getRow(); x++) {
                if (inventory.getItem(Slot.get(x,y)) == null) continue;
                storageItems.put(Slot.get(x,y), inventory.getItem(Slot.get(x,y)));
            }
        }

        return this;

    }
    @Override
    public String toString() {
        JsonArray jsonArray = new JsonArray();

        for (Map.Entry<Integer, ItemStack> entry : storageItems.entrySet()) {
            JsonObject item = new JsonObject();
            item.addProperty("slot", entry.getKey());
            item.addProperty("item", ItemBuilder.toString(entry.getValue()));
            jsonArray.add(item);
        }

        return jsonArray.toString();
    }

    public static Map<Integer, ItemStack> fromString(String json) {
        Map<Integer, ItemStack> items = new HashMap<>();

        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (JsonElement object : array) items.put(object.getAsJsonObject().get("slot").getAsInt(), ItemBuilder.fromString(object.getAsJsonObject().get("item").getAsString()));

        return items;
    }
}
