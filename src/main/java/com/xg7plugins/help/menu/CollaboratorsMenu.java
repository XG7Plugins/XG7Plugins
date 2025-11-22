package com.xg7plugins.help.menu;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.setup.Collaborator;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.impl.SkullItem;
import org.bukkit.entity.Player;

import java.util.*;

public class CollaboratorsMenu extends PagedMenu {

    private final Plugin plugin;
    private final HelpGUI guiOrigin;

    public CollaboratorsMenu(HelpGUI gui, Plugin plugin) {
        super(MenuConfigurations.of(
                XG7Plugins.getInstance(),
                "collaborators",
                "lang:[collaborators-menu.title]",
                (plugin.getPluginSetup().collaborators().length / 7) + 3,
                EnumSet.noneOf(MenuAction.class),
                true,
                Collections.singletonList(Pair.of("plugin", plugin.getName()))
        ), Slot.of(2, 2), Slot.of(plugin.getPluginSetup().collaborators().length / 7 + 2, 8));
        this.plugin = plugin;
        this.guiOrigin = gui;
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        Collaborator[] collaborators = plugin.getPluginSetup().collaborators();

        List<InventoryItem> items = new ArrayList<>();

        for (Collaborator collaborator : collaborators) {
            items.add(SkullItem.newSkull().setSkinByUUID(UUID.fromString(collaborator.uuid())).name(collaborator.name()).lore(collaborator.role()).toInventoryItem(-1, true));
        }

        return items;
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return Collections.singletonList(CloseInventoryItem.get(Slot.fromSlot((getMenuConfigs().getRows() - 1) * 9), guiOrigin.getMenu("index")));
    }
}
