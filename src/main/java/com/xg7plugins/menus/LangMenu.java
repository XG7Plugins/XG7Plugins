package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class LangMenu {

    @Getter
    protected static HashMap<UUID,Long> cooldownToToggle = new HashMap<>();

    public static void create(Player player) {

        try {
            XG7Plugins plugin = XG7Plugins.getInstance();

            if (plugin.getMenuManager().cacheExistsPlayer("lang", player)) {
                ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("lang", player);

                menu.open();
                return;
            }

            plugin.getLangManager().loadAllLangs();

            Config config = plugin.getConfigsManager().getConfig("config");

            if (XG7Plugins.isFloodgate() && (boolean) config.get("enable-lang-form")) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    LangForm.create(player);
                    return;
                }
            }


            PlayerLanguage language = plugin.getLangManager().getPlayerLanguageDAO().get(player.getUniqueId()).join();


            List<BaseItemBuilder<?>> items = new ArrayList<>();
            plugin.getLangManager().getLangs().asMap().forEach((s, c)-> {
                try {
                    BaseItemBuilder<?> builder = BaseItemBuilder.from(c.getString("icon"), plugin);
                    boolean selected = language != null && language.getLangId().equals(s);

                    builder.name(c.getString("formated-name") != null ? selected ? "§a" + c.getString("formated-name") : "§7" + c.getString("formated-name") : selected ? "§a" + s : "§7" + s);
                    builder.lore(c.getStringList("lang-menu.item-click"));
                    builder.click(event -> {


                        if (language != null && language.getLangId().equals(s)) {
                            Text.formatComponent("lang:[lang-menu.already-selected]", plugin).send(player);
                            return;
                        }

                        cooldownToToggle.putIfAbsent(player.getUniqueId(), 0L);

                        if (cooldownToToggle.get(player.getUniqueId()) >= System.currentTimeMillis()) {
                            Text.formatComponent("lang:[lang-menu.cooldown-to-toggle]",plugin)
                                    .replace("[MILLISECONDS]", String.valueOf((cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis())))
                                    .replace("[SECONDS]", String.valueOf((int)((cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)))
                                    .replace("[MINUTES]", String.valueOf((int)((cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 60000)))
                                    .replace("[HOURS]", String.valueOf((int)((cooldownToToggle.get(player.getUniqueId()) - System.currentTimeMillis()) / 3600000)))
                                    .send(player);
                            return;
                        }

                        plugin.getLangManager().getPlayerLanguageDAO().update(new PlayerLanguage(player.getUniqueId(),s)).thenAccept(r -> {
                            plugin.getMenuManager().removePlayerFromAll(player);
                            create(player);
                            Text.formatComponent("lang:[lang-menu.toggle-success]", plugin).send(player);
                        });
                        plugin.getPlugins().forEach((n, pl) -> pl.getLangManager().getPlayerLanguageDAO().update(new PlayerLanguage(player.getUniqueId(),s)));


                        cooldownToToggle.put(player.getUniqueId(), System.currentTimeMillis() + Text.convertToMilliseconds(plugin, config.get("cooldown-to-toggle-lang")));

                    });

                    items.add(builder);
                } catch (Exception e){
                    e.printStackTrace();
                }

            });

            PageMenuBuilder builder = MenuBuilder.page("lang")
                    .title("lang:[lang-menu.title]")
                    .rows(6)
                    .setArea(Slot.of(2,2), Slot.of(5,8))
                    .setItems(items)
                    .setItem(49, ItemBuilder.from(XMaterial.BARRIER.parseItem(), plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()));
            int langSize = plugin.getLangManager().getLangs().asMap().size();

            if (langSize > 24) {
                builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
                builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
            }
            builder.build(player, plugin).open();
        } catch (Throwable e) {
            e.printStackTrace();
        }



    }




}
