package com.xg7plugins.modules.xg7menus.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.impl.EnchantedBookItem;
import com.xg7plugins.modules.xg7menus.item.parser.ItemParser;

import java.util.Collections;
import java.util.List;

public class EnchantedBookParser implements ItemParser<EnchantedBookItem> {
    @Override
    public List<XMaterial> getMaterials() {
        return Collections.singletonList(XMaterial.ENCHANTED_BOOK);
    }

    @Override
    public EnchantedBookItem parse(XMaterial material, String... args) {

        if (args.length == 0) return EnchantedBookItem.newEnchantedBook();

        String value = args[0];

        int level = 1;

        if (args.length > 1) level = Integer.parseInt(args[1]);

        return EnchantedBookItem.newEnchantedBook().addEnchantment(value, level);
    }
}
