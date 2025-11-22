package com.xg7plugins.menus.lang;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

public class LangItem extends ClickableItem {
    public LangItem(Item item) {
        super(item.getItemStack(), null);
    }

    @Override
    public void onClick(ActionEvent event) {

        Player player = event.getHolder().getPlayer();

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        String langName = event.getClickedItem().getTag("lang-id", String.class).orElse(null);
        boolean selected = event.getClickedItem().getTag("selected", Boolean.class).orElse(false);

        if (selected) {
            Text.sendTextFromLang(player, holder.getMenu().getMenuConfigs().getPlugin(), "lang-menu.already-selected");
            return;
        }

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("lang-change", player)) {

            long cooldownToToggle = XG7Plugins.getAPI().cooldowns().getReamingTime("lang-change", player);

            Text.sendTextFromLang(player, holder.getMenu().getMenuConfigs().getPlugin(), "lang-menu.cooldown-to-toggle", Pair.of("time", String.valueOf((cooldownToToggle))));

            return;
        }

        PlayerDataRepository dao = XG7Plugins.getAPI().getRepository(PlayerDataRepository.class);

        dao.getAsync(player.getUniqueId()).thenAccept((data) -> {
            String dbLang = langName.split(":")[1];

            data.setLangId(dbLang);

            dao.update(data);
            XG7Plugins.getAPI().langManager().loadLangsFrom(holder.getMenu().getMenuConfigs().getPlugin()).join();
            Text.sendTextFromLang(player, holder.getMenu().getMenuConfigs().getPlugin(), "lang-menu.toggle-success");
            PagedMenu.refresh(holder);
            XG7Plugins.getAPI().scores().removePlayer(player);
            XG7Plugins.getAPI().scores().addPlayer(player);


            XG7Plugins.getAPI().cooldowns().addCooldown(player, "lang-change", ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("cooldown-to-toggle-lang", 5000L));

        });
    }
}
