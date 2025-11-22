package com.xg7plugins.utils.item.impl;


import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class PotionConsumableItem extends Item {

    private PotionType type;
    private boolean isExtended;
    private boolean isStrong;

    private boolean isSplash;

    public PotionConsumableItem(ItemStack itemStack) {
        super(itemStack);

        if (!Arrays.asList(Material.POTION, Material.getMaterial("SPLASH_POTION"), Material.getMaterial("LINGERING_POTION"), Material.getMaterial("TIPPED_ARROW")).contains(itemStack.getType()))
            throw new IllegalArgumentException("This item isn't a potion consumable item!");
    }


    public static PotionConsumableItem from(ItemStack itemStack) {
        return new PotionConsumableItem(itemStack);
    }

    public static PotionConsumableItem newPotion() {
        return new PotionConsumableItem(new ItemStack(Material.POTION));
    }
    public static PotionConsumableItem newSplashPotion() {
        if (Material.getMaterial("SPLASH_POTION") == null) return newPotion().splash(true);
        return new PotionConsumableItem(new ItemStack(Material.SPLASH_POTION));
    }
    public static PotionConsumableItem newLingeringPotion() {
        return new PotionConsumableItem(new ItemStack(Material.LINGERING_POTION));
    }
    public static PotionConsumableItem newTippedArrow() {
        return new PotionConsumableItem(new ItemStack(Material.TIPPED_ARROW));
    }

    public PotionConsumableItem splash(boolean splash) {
        this.isSplash = splash;
        return this;
    }

    public PotionConsumableItem type(PotionType type) {
        this.type = type;
        return this;
    }

    public PotionConsumableItem strong(boolean strong) {
        this.isStrong = strong;
        return this;
    }
    public PotionConsumableItem extended(boolean extended) {
        this.isExtended = extended;
        return this;
    }

    private void applyEffects() {
        try {
            PotionMeta meta = (PotionMeta) this.itemStack.getItemMeta();
            meta.setBasePotionData(new PotionData(type, isExtended, isStrong));
            super.meta(meta);
        } catch (Throwable ignored) {
            Potion potion = new Potion(type);
            if (isExtended && isStrong) isExtended = false;

            if (isExtended) potion.extend();
            if (this.isSplash) potion.splash();

            potion.apply(this.itemStack);
        }
    }

    @Override
    public ItemStack getItemStack() {
        applyEffects();
        return this.itemStack;
    }

    @Override
    public ItemStack getItemFor(CommandSender player, Plugin plugin) {
        applyEffects();
        return super.getItemFor(player, plugin);
    }

}
