package com.xg7plugins.modules.xg7menus.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.impl.PotionConsumableItem;
import com.xg7plugins.modules.xg7menus.item.parser.ItemParser;
import com.xg7plugins.utils.Parser;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class PotionParser implements ItemParser<PotionConsumableItem> {
    @Override
    public List<XMaterial> getMaterials() {
        return Arrays.asList(XMaterial.POTION, XMaterial.SPLASH_POTION, XMaterial.LINGERING_POTION, XMaterial.TIPPED_ARROW);
    }

    @Override
    public PotionConsumableItem parse(XMaterial material, String... args) {

        if (!material.isSupported()) return PotionConsumableItem.from(Item.from(Material.POTION).getItemStack());

        if (args.length < 1) {
            return PotionConsumableItem.from(Item.from(material).getItemStack()).type(PotionType.WATER);
        }

        

        PotionConsumableItem item = PotionConsumableItem.from(Item.from(material).getItemStack());

        String potion = args[0];

        boolean isStrong = args.length >= 2 && (boolean) Parser.BOOLEAN.convert(args[1]);
        boolean isExtended = args.length >= 3 && (boolean) Parser.BOOLEAN.convert(args[2]);

        PotionType potionType = PotionType.valueOf(potion.toUpperCase());

        return item.type(potionType).strong(isStrong).extended(isExtended);
    }
}
