package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    public List<Item> pagedItems(Player player) {

        LangManager manager = XG7PluginsAPI.langManager();

        manager.loadLangsFrom(XG7Plugins.getInstance()).join();

        PlayerData language = XG7PluginsAPI.getRepository(PlayerDataRepository.class).get(player.getUniqueId());

        List<Item> pagedItems = new ArrayList<>();

        manager.getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {
            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            pagedItems.add(map.getValue().getLangConfiguration().get("", Item.air(), map.getKey(), selected));
        });

        return pagedItems;
    }

    @Override
    public List<Item> getItems(Player player) {
        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        editor.setItem(Slot.fromSlot(45), Item.from(XMaterial.ARROW).name("lang:[go-back-item]"));
        editor.setItem(Slot.fromSlot(49), Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[close-item]"));
        editor.setItem(Slot.fromSlot(53), Item.from(XMaterial.ARROW).name("lang:[go-next-item]"));

        return editor.getItems();
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        Player player = event.getHolder().getPlayer();

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        switch (event.getClickedSlot().get()) {
            case 45:
                holder.previousPage();
                break;
            case 49:
                player.closeInventory();
                break;
            case 53:
                holder.nextPage();
                break;
            default:
                if (event.getClickedSlot() == null || event.getClickedItem().isAir()) return;

                String langName = event.getClickedItem().getTag("lang-id", String.class).orElse(null);
                boolean selected = event.getClickedItem().getTag("selected", Boolean.class).orElse(false);

                if (selected) {
                    Text.sendTextFromLang(player, getMenuConfigs().getPlugin(), "lang-menu.already-selected");
                    return;
                }

                if (XG7PluginsAPI.cooldowns().containsPlayer("lang-change", player)) {

                    long cooldownToToggle = XG7PluginsAPI.cooldowns().getReamingTime("lang-change", player);

                    Text.sendTextFromLang(player, getMenuConfigs().getPlugin(), "lang-menu.cooldown-to-toggle", Pair.of("time", String.valueOf((cooldownToToggle))));

                    return;
                }

                PlayerDataRepository dao = XG7PluginsAPI.getRepository(PlayerDataRepository.class);

                dao.getAsync(player.getUniqueId()).thenAccept((data) -> {
                    String dbLang = langName.split(":")[1];

                    data.setLangId(dbLang);

                    dao.update(data);
                    XG7PluginsAPI.langManager().loadLangsFrom(getMenuConfigs().getPlugin()).join();
                    Text.sendTextFromLang(player, getMenuConfigs().getPlugin(), "lang-menu.toggle-success");
                    refresh(holder);
                    XG7Scores.getInstance().removePlayer(player);
                    XG7Scores.getInstance().addPlayer(player);


                    XG7PluginsAPI.cooldowns().addCooldown(player, "lang-change", ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("cooldown-to-toggle-lang", 5000L));

                });
        }
    }
}
