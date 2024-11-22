package com.xg7plugins.libs.xg7menus.builders.menu;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerMenuBuilder extends BaseMenuBuilder<PlayerMenu,PlayerMenuBuilder> {

    public PlayerMenuBuilder(String id) {
        super(id);
    }

    @Override
    public PlayerMenu build(Object... args) {

        Player player = (Player) args[0];

        Map<Integer, ItemStack> buildItems = new HashMap<>();
        items.forEach((slot, itemBuilder) -> {
            if (itemBuilder instanceof SkullItemBuilder) {
                SkullMeta meta = (SkullMeta) itemBuilder.toItemStack().getItemMeta();
                if ("THIS_PLAYER".equals(meta.getOwner())) buildItems.put(slot, ((SkullItemBuilder) itemBuilder).setOwner(player.getName()).setPlaceHolders(player).toItemStack());
            }
            buildItems.put(slot, ((ItemBuilder) itemBuilder).setBuildReplacements(itemBuilder.getBuildReplacements()).setPlaceHolders(player).toItemStack());
        });

        return new PlayerMenu(id,defaultClickEvent,openMenuEvent,closeMenuEvent, buildItems, clickEventMap, allowedPermissions, player);
    }
}
