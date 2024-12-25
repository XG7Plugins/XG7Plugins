package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.data.lang.PlayerLanguageDAO;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.libs.xg7menus.menus.holders.PageMenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangMenu extends PageMenu {
    public LangMenu(XG7Plugins plugin) {
        super(plugin, "lang-menu", "lang:[lang-menu.title]", 54, Slot.of(2,2), Slot.of(5,8));
    }

    @Override
    public List<Item> pagedItems(Player player) {

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();

        PlayerLanguage language = XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO().get(player.getUniqueId()).join();

        List<Item> pagedItems = new ArrayList<>();

        XG7Plugins.getInstance().getLangManager().getLangs().asMap().forEach((s, c)-> {
            boolean selected = language != null && language.getLangId().equals(s);

            pagedItems.add(Config.of(XG7Plugins.getInstance(), c).get("", Item.class, selected, s).orElse(null));
        });

        return pagedItems;
    }

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfig("config").get("enable-langs", Boolean.class).orElse(false);
    }

    @Override
    protected List<Item> items(Player player) {
        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.BARRIER).name("lang:[close-item]").slot(49),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53)
        );
    }

    @Override
    public void onClick(MenuEvent event) {
        if (event instanceof ClickEvent) {
            ClickEvent clickEvent = (ClickEvent) event;
            Player player = (Player) clickEvent.getWhoClicked();

            PageMenuHolder holder = (PageMenuHolder) clickEvent.getInventoryHolder();

            switch (clickEvent.getClickedSlot()) {
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
                    if (clickEvent.getClickedItem() == null || clickEvent.getClickedItem().isAir()) return;

                    String langName = clickEvent.getClickedItem().getTag("lang-id", String.class).orElse(null);
                    boolean selected = clickEvent.getClickedItem().getTag("selected", Boolean.class).orElse(false);

                    if (selected) {
                        Text.formatComponent("lang:[lang-menu.already-selected]", plugin).send(player);
                        return;
                    }

                    if (XG7Plugins.getInstance().getCooldownManager().containsPlayer("lang-change", player)) {

                        double cooldownToToggle = XG7Plugins.getInstance().getCooldownManager().getReamingTime("lang-change", player);

                        Text.formatComponent("lang:[lang-menu.cooldown-to-toggle]",plugin)
                                .replace("[MILLISECONDS]", String.valueOf((cooldownToToggle - System.currentTimeMillis())))
                                .replace("[SECONDS]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 1000)))
                                .replace("[MINUTES]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 60000)))
                                .replace("[HOURS]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 3600000)))
                                .send(player);
                    }

                    PlayerLanguageDAO dao = XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO();

                    dao.update(new PlayerLanguage(player.getUniqueId(), langName)).thenAccept(r -> {
                        XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
                        Text.formatComponent("lang:[lang-menu.changed]", plugin).send(player);
                        refresh(holder);
                    });

                    XG7Plugins.getInstance().getCooldownManager().addCooldown(player, "lang-change", XG7Plugins.getInstance().getConfig("config").getTime("cooldown-to-toggle-lang").orElse(10000L));

            }
        }
    }
}
