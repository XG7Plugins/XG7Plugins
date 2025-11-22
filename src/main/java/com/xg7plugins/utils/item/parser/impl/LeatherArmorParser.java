package com.xg7plugins.utils.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.item.parser.ItemParser;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;

public class LeatherArmorParser implements ItemParser<Item> {
    @Override
    public List<XMaterial> getMaterials() {
        return Arrays.asList(XMaterial.LEATHER_HELMET, XMaterial.LEATHER_CHESTPLATE, XMaterial.LEATHER_LEGGINGS, XMaterial.LEATHER_BOOTS);
    }

    @Override
    public Item parse(XMaterial material, String... args) {

        if (args == null || args.length == 0) return Item.from(material);

        Item item = Item.from(material);

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemStack().getItemMeta();

        String color = args[0];
        if (color.startsWith("#")) color = color.substring(1);

        if (color.length() != 6) {
            throw new IllegalArgumentException("Hex string must have 6 characters (RRGGBB)");
        }

        int r = Integer.parseInt(color.substring(0, 2), 16);
        int g = Integer.parseInt(color.substring(2, 4), 16);
        int b = Integer.parseInt(color.substring(4, 6), 16);

        meta.setColor(Color.fromRGB(r, g, b));
        return item.meta(meta);
    }
}
