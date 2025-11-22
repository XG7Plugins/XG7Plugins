package com.xg7plugins.utils.item.impl;

import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerItem extends Item {
    public BannerItem() {
        super(new ItemStack(Material.WHITE_BANNER));
    }

    public BannerItem(ItemStack itemStack) {
        super(itemStack);
        if (!itemStack.getType().name().contains("BANNER")) throw new IllegalArgumentException("This item isn't a banner!");
    }

    public static BannerItem from(ItemStack itemStack) {
        return new BannerItem(itemStack);
    }

    public static BannerItem from(Material style) {
        return new BannerItem(new ItemStack(style));
    }

    public static BannerItem newWhiteBanner() {
        return new BannerItem();
    }

    public BannerItem addPattern(Pattern pattern) {
        BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
        meta.addPattern(pattern);
        super.meta(meta);
        return this;
    }


}
