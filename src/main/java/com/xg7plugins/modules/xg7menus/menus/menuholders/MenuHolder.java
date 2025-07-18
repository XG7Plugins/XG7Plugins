package com.xg7plugins.modules.xg7menus.menus.menuholders;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.tasks.tasks.BukkitTask;
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
        MenuConfigurations menuConfigurations = menu.getMenuConfigs();

        this.inventory = menuConfigurations.getInventoryType() == null ?
                Bukkit.createInventory(this, menuConfigurations.getRows() * 9, Text.detectLangs(player, menuConfigurations.getPlugin(), menuConfigurations.getTitle()).join().replaceAll(menuConfigurations.getPlaceholders()).getText())
                :
                Bukkit.createInventory(this, menuConfigurations.getInventoryType(), Text.detectLangs(player, menuConfigurations.getPlugin(), menuConfigurations.getTitle()).join().replaceAll(menuConfigurations.getPlaceholders()).getText());

        this.inventoryUpdater = new InventoryUpdater(this);

            player.closeInventory();

            player.openInventory(inventory);

            XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Plugins.getInstance(), () -> BasicMenu.refresh(this)), 100L);
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
