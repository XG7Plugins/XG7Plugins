package com.xg7plugins.utils.item.impl;

import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantedBookItem extends Item {
    public EnchantedBookItem() {
        super(new ItemStack(Material.ENCHANTED_BOOK));
    }
    public EnchantedBookItem(ItemStack book) {
        super(book);
        if (!book.getType().equals(Material.ENCHANTED_BOOK)) throw new IllegalArgumentException("This item isn't an enchanted book!");
    }

    public static EnchantedBookItem from(ItemStack itemStack) {
        return new EnchantedBookItem(itemStack);
    }

    public static EnchantedBookItem newEnchantedBook() {
        return new EnchantedBookItem();
    }

    public EnchantedBookItem addEnchantment(String enchantment, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        meta.addStoredEnchant(Enchantment.getByName(enchantment.toUpperCase()), level, true);
        super.meta(meta);
        return this;
    }
}
