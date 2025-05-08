package com.xg7plugins.plugin_menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.IMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.PagedMenu;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LangMenu extends PagedMenu {
    public LangMenu() {
        super(IMenuConfigurations.of(
                XG7Plugins.getInstance(),
                "lang-menu",
                "lang:[lang-menu.title]",
                6,
                XG7Plugins.getInstance().getConfig("config").get("enable-langs", Boolean.class).orElse(false)
            ), Slot.of(2,2), Slot.of(5,8));
    }

    @Override
    public List<Item> pagedItems(Player player) {

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        PlayerData language = XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).join();

        List<Item> pagedItems = new ArrayList<>();

        XG7Plugins.getInstance().getLangManager().getLangs().asMap().join().entrySet().stream().filter(entry -> entry.getKey().contains("XG7Plugins")).forEach((map)-> {
            boolean selected = language != null && language.getLangId().equals(map.getKey().contains(":") ? map.getKey().split(":")[1] : map.getKey());

            pagedItems.add(map.getValue().get("", Item.class, map.getKey(),selected).orElse(Item.air()));
        });

        return pagedItems;
    }

    @Override
    public List<Item> getItems(Player player) {
        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[close-item]").slot(49),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53)
        );
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        Player player = event.getHolder().getPlayer();

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        switch (event.getSlotClicked().get()) {
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
                if (event.getSlotClicked() == null || event.getClickedItem().isAir()) return;

                String langName = event.getClickedItem().getTag("lang-id", String.class).orElse(null);
                boolean selected = event.getClickedItem().getTag("selected", Boolean.class).orElse(false);

                if (selected) {
                    Text.fromLang(player, plugin, "lang-menu.already-selected").thenAccept(text -> text.send(player));
                    return;
                }

                if (XG7Plugins.getInstance().getCooldownManager().containsPlayer("lang-change", player)) {

                    double cooldownToToggle = XG7Plugins.getInstance().getCooldownManager().getReamingTime("lang-change", player);

                    Text.fromLang(player, plugin, "lang-menu.cooldown-to-toggle").thenAccept(
                            text -> text.replace("milliseconds", String.valueOf((cooldownToToggle)))
                                    .replace("seconds", String.valueOf((int) ((cooldownToToggle) / 1000)))
                                    .replace("minutes", String.valueOf((int) ((cooldownToToggle) / 60000)))
                                    .replace("hours", String.valueOf((int) ((cooldownToToggle) / 3600000)))
                                    .send(player)
                    );

                    return;
                }

                PlayerDataDAO dao = XG7Plugins.getInstance().getPlayerDataDAO();

                PlayerData data = dao.get(player.getUniqueId()).join();

                String dbLang = langName.split(":")[1];

                data.setLangId(dbLang);

                dao.update(data).thenAccept(r -> {
                    XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin).join();
                    Text.fromLang(player, plugin, "lang-menu.toggle-success").thenAccept(text -> text.send(player));
                    player.closeInventory();
                    open(player);
                    refresh(holder);
                });

                XG7Plugins.getInstance().getCooldownManager().addCooldown(player, "lang-change", XG7Plugins.getInstance().getConfig("config").getTime("cooldown-to-toggle-lang").orElse(5000L));

        }
    }
}
