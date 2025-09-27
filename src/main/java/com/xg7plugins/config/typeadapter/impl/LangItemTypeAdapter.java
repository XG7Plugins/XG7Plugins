package com.xg7plugins.config.typeadapter.impl;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7menus.item.Item;

import java.util.ArrayList;

public class LangItemTypeAdapter implements ConfigTypeAdapter<Item> {

    @Override
    public Item fromConfig(ConfigSection config, String path, Object... args) {

        if (args.length != 2 && !(args[0] instanceof String) && !(args[1] instanceof Boolean)) throw new IllegalArgumentException("Invalid arguments");

        boolean selected = (boolean) args[1];
        String langid = (String) args[0];

        Item langItem = Item.from(config.get("icon", "STONE"));

        String name = config.get("formated-name", "LANG NOT NAMED");

        langItem.name(selected ? "&a" + name : "&7" + name);
        langItem.lore(config.getList("lang-menu.item-click", String.class).orElse(new ArrayList<>()));

        langItem.setNBTTag("selected", selected);
        langItem.setNBTTag("lang-id", langid);
        return langItem;

    }

    @Override
    public Class<Item> getTargetType() {
        return Item.class;
    }
}
