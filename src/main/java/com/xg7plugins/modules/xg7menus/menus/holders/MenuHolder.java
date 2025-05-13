package com.xg7plugins.modules.xg7menus.menus.holders;

import com.xg7plugins.modules.xg7menus.menus.IBasicMenu;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.IMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.Menu;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public class MenuHolder extends BasicMenuHolder implements InventoryHolder {

    private final Inventory inventory;
    private final InventoryUpdater inventoryUpdater;

    public MenuHolder(Menu menu, Player player) {
        super(menu, player);
        IMenuConfigurations menuConfigurations = menu.getMenuConfigs();

       this.inventory = menuConfigurations.getInventoryType() == null ?
               Bukkit.createInventory(this, menuConfigurations.getRows() * 9, Text.detectLangs(player, menuConfigurations.getPlugin(), menuConfigurations.getTitle()).join().replaceAll(menuConfigurations.getPlaceholders()).getText())
               :
               Bukkit.createInventory(this, menuConfigurations.getInventoryType(), Text.detectLangs(player, menuConfigurations.getPlugin(), menuConfigurations.getTitle()).join().replaceAll(menuConfigurations.getPlaceholders()).getText());

       this.inventoryUpdater = new InventoryUpdater(this);

       player.openInventory(inventory);

       IBasicMenu.refresh(this);

    }


    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public InventoryUpdater getInventoryUpdater() {
        return inventoryUpdater;
    }
}
