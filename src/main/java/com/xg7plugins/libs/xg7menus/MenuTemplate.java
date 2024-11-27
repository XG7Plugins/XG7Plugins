package com.xg7plugins.libs.xg7menus;

import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public interface MenuTemplate<M extends BaseMenu> {

    List<BaseItemBuilder> getItems();

    void onOpen(Player player);
    void onClose(Player player);

    M build(Player player, HashMap<Integer, BaseItemBuilder<?>> items);

}
