package com.xg7plugins.libs.xg7menus.builders.menu;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class PlayerMenuBuilder extends BaseMenuBuilder<PlayerMenuBuilder> {

    public PlayerMenuBuilder(String id) {
        super(id);
    }

    @Override
    public PlayerMenu build(Player player, Plugin plugin) {
        return new PlayerMenu(id,defaultClickEvent,openMenuEvent,closeMenuEvent,
                items.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    BaseItemBuilder<?> builder = entry.getValue();
                                    if (builder instanceof SkullItemBuilder) {
                                        SkullMeta meta = (SkullMeta) builder.toItemStack().getItemMeta();
                                        if ("THIS_PLAYER".equals(meta.getOwner())) return ((SkullItemBuilder) builder).setOwner(player.getName()).setPlaceHolders(player).toItemStack();
                                    }
                                    return builder.setBuildReplacements(builder.getBuildReplacements()).setPlaceHolders(player).toItemStack();
                                }

                        )), clickEventMap, allowedPermissions, player);
    }
}
