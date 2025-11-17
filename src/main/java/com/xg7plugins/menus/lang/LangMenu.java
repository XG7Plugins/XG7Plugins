package com.xg7plugins.menus.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import org.bukkit.entity.Player;

import java.util.*;

public class LangMenu extends PagedMenu {

    public LangMenu() {
        super(MenuConfigurations.of(
                XG7Plugins.getInstance(),
                "lang-menu",
                "lang:[lang-menu.title]",
                6,
                ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("lang-enabled", false)
            ), Slot.of(2,2), Slot.of(5,8));
    }

    @Override
    public List<InventoryItem> pagedItems(Player player) {

        LangManager manager = XG7PluginsAPI.langManager();

        manager.loadLangsFrom(XG7Plugins.getInstance()).join();

        PlayerData language = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

        List<InventoryItem> pagedItems = new ArrayList<>();

        manager.getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {
            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            pagedItems.add(map.getValue().getLangConfiguration().get("", InventoryItem.air().toInventoryItem(null), map.getKey(), selected));
        });

        return pagedItems;
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return Arrays.asList(
                ChangePageItem.nextPageItem(Slot.fromSlot(45)).name("lang:[go-back-item]"),
                CloseInventoryItem.get(Slot.fromSlot(49)).name("lang:[close-item]"),
                ChangePageItem.previousPageItem(Slot.fromSlot(53)).name("lang:[go-next-item]")
        );
    }
}
