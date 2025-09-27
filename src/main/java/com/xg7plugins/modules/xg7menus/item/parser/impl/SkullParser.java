package com.xg7plugins.modules.xg7menus.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.impl.SkullItem;
import com.xg7plugins.modules.xg7menus.item.parser.ItemParser;

import java.util.Collections;
import java.util.List;

public class SkullParser implements ItemParser<SkullItem> {
    @Override
    public List<XMaterial> getMaterials() {
        return Collections.singletonList(XMaterial.PLAYER_HEAD);
    }

    @Override
    public SkullItem parse(XMaterial material, String... args) {

        if (args.length != 1) {
            return SkullItem.newSkull();
        }

        String value = args[0];

        if (value.startsWith("eyJ0")) return SkullItem.newSkull().setValue(value);
        if (value.equals("THIS_PLAYER")) return SkullItem.newSkull().renderPlayerSkull(true);

        return null;
    }
}
