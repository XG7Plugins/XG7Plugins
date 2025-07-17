package com.xg7plugins.modules.xg7menus.menus.menuholders;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

@Getter
public class PlayerMenuHolder extends BasicMenuHolder {

    private final InventoryUpdater inventoryUpdater;
    private final HashMap<Integer, ItemStack> oldItems = new HashMap<>();

    public PlayerMenuHolder(PlayerMenu menu, Player player) {
        super(menu, player);

        this.inventoryUpdater = new InventoryUpdater(this);

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null) oldItems.put(i, item);
        }

        BasicMenu.refresh(this);
    }

    @Override
    public PlayerInventory getInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public InventoryUpdater getInventoryUpdater() {
        return inventoryUpdater;
    }

    @Override
    public PlayerMenu getMenu() {
        return (PlayerMenu) super.getMenu();
    }
}
