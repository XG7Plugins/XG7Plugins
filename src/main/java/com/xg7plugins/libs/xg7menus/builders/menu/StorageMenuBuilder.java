package com.xg7plugins.libs.xg7menus.builders.menu;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.MenuException;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.DragEvent;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.libs.xg7menus.menus.gui.StorageMenu;
import com.xg7plugins.libs.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StorageMenuBuilder extends BaseMenuBuilder<StorageMenu, StorageMenuBuilder> {

    protected String title;
    protected int size;
    protected InventoryType type;

    private Map<Integer, ItemStack> storageItems = new HashMap<>();
    private Slot initStorageSlot;
    private Slot finalStorageSlot;

    public StorageMenuBuilder(String id) {
        super(id);
    }

    public StorageMenuBuilder title(String title) {
        this.title = title;
        return this;
    }
    public StorageMenuBuilder size(int size) {
        this.size = size;
        return this;
    }
    public StorageMenuBuilder rows(int rows) {
        this.size = rows * 9;
        return this;
    }
    public StorageMenuBuilder type(InventoryType type) {
        this.type = type;
        return this;
    }
    public StorageMenuBuilder setStorageItems(Map<Integer, ItemStack> storageItems) {
        this.storageItems = storageItems;
        return this;
    }
    public StorageMenuBuilder setInitStorageSlot(Slot initStorageSlot) {
        this.initStorageSlot = initStorageSlot;
        return this;
    }
    public StorageMenuBuilder setFinalStorageSlot(Slot finalStorageSlot) {
        this.finalStorageSlot = finalStorageSlot;
        return this;
    }
    public StorageMenuBuilder setStorageArea(Slot initStorageSlot, Slot finalStorageSlot) {
        this.initStorageSlot = initStorageSlot;
        this.finalStorageSlot = finalStorageSlot;
        return this;
    }

    @Override
    public StorageMenu build(Object... args) {
        if (title == null) throw new MenuException("The inventory must have a title!");

        Player player = (Player) args[0];
        Plugin plugin = (Plugin) args[1];
        if (initStorageSlot == null || finalStorageSlot == null) throw new MenuException("The inventory must have a area to put storedItems!");

        Map<Integer, ItemStack> buildItems = new HashMap<>();
        items.forEach((slot, itemBuilder) -> {
            if (itemBuilder instanceof SkullItemBuilder) {
                if (XG7Plugins.getMinecraftVersion() > 7) {
                    SkullMeta meta = (SkullMeta) itemBuilder.toItemStack().getItemMeta();
                    if ("THIS_PLAYER".equals(meta.getOwner()))
                        buildItems.put(slot, ((SkullItemBuilder) itemBuilder).setOwner(player.getName()).setPlaceHolders(player, itemBuilder.getBuildReplacements()).toItemStack());
                }
                buildItems.put(slot, ((SkullItemBuilder) itemBuilder).setPlaceHolders(player, itemBuilder.getBuildReplacements()).toItemStack());
                return;
            }
            buildItems.put(slot, ((ItemBuilder) itemBuilder).setPlaceHolders(player, itemBuilder.getBuildReplacements()).toItemStack());
        });
        if (defaultClickEvent == null) setDefaultClickEvent(event -> {
            if (event.getClickAction().equals(ClickEvent.ClickAction.DRAG)) {
                event.setCancelled(((DragEvent)event).getClickedSlots().stream().anyMatch(slot -> {
                    Slot clickSlot = Slot.fromSlot(slot);
                    return !(clickSlot.getRow() >= initStorageSlot.getRow() && clickSlot.getRow() <= finalStorageSlot.getRow() && clickSlot.getColumn() >= initStorageSlot.getColumn() -1 && clickSlot.getColumn() <= finalStorageSlot.getColumn());
                }));
                return;
            }
            Slot clickSlot = Slot.fromSlot(event.getClickedSlot());
            event.setCancelled(!(clickSlot.getRow() >= initStorageSlot.getRow() && clickSlot.getRow() <= finalStorageSlot.getRow() && clickSlot.getColumn() >= initStorageSlot.getColumn() -1 && clickSlot.getColumn() <= finalStorageSlot.getColumn()));
        });

        return type == null ? new StorageMenu(id,Text.getCentralizedText(Text.PixelsSize.INV.getPixels(),Text.format(title,plugin).getWithPlaceholders(player)), size, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player, initStorageSlot, finalStorageSlot, storageItems) : new StorageMenu(id,Text.getCentralizedText(Text.PixelsSize.INV.getPixels(),Text.format(title,plugin).getWithPlaceholders(player)), type, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player, initStorageSlot, finalStorageSlot, storageItems);

    }
}
