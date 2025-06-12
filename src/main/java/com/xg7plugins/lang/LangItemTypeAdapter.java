package com.xg7plugins.lang;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7menus.item.Item;

import java.util.ArrayList;

public class LangItemTypeAdapter implements ConfigTypeAdapter<Item> {

    @Override
    public Item fromConfig(Config config, String path, Object... args) {

        if (args.length != 2 && !(args[0] instanceof String) && !(args[1] instanceof Boolean)) throw new IllegalArgumentException("Invalid arguments");

        boolean selected = (boolean) args[1];
        String langid = (String) args[0];

        Item langItem = Item.from(config.get("icon", String.class).orElse("STONE"));

        String name = config.get("formated-name", String.class).orElse("LANG NOT NAMED");

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
