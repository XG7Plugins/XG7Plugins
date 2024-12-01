package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.item.Item;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class PageMenu extends Menu {

    private final HashMap<UUID, Integer> currentPage = new HashMap<>();
    private final Slot pos1;
    private final Slot pos2;
    private final List<Item> itemList;

    public PageMenu(Plugin plugin, String id, String title, int size, Slot pos1, Slot pos2, List<Item> itemList) {
        super(plugin, id, title, size);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.itemList = itemList;

    }

    public PageMenu(Plugin plugin, String id, String title, InventoryType type, Slot pos1, Slot pos2, List<Item> itemList) {
        super(plugin, id, title, type);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.itemList = itemList;
    }

    public static void goPage() {}


    @Override
    public void open(Player player) {
        currentPage.putIfAbsent(player.getUniqueId(), 0);

    }


}
