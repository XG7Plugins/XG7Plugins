package com.xg7plugins.modules.xg7menus.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.impl.FireworkItem;
import com.xg7plugins.modules.xg7menus.item.parser.ItemParser;

import java.util.Collections;
import java.util.List;

public class SimpleFireworkParser implements ItemParser<FireworkItem> {
    @Override
    public List<XMaterial> getMaterials() {
        return Collections.singletonList(XMaterial.FIREWORK_ROCKET);
    }

    @Override
    public FireworkItem parse(XMaterial material, String... args) {

        if (args.length == 0) return FireworkItem.newFirework();

        FireworkItem item = FireworkItem.newFirework();

        item.power(Integer.parseInt(args[0]));

        return item;
    }
}
