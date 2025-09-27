package com.xg7plugins.modules.xg7menus.item.impl;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class FireworkStarItem extends Item {

    private final FireworkEffect.Builder builder = FireworkEffect.builder();


    public FireworkStarItem() {
        super(new ItemStack(Material.FIREWORK_STAR));
    }

    public FireworkStarItem(ItemStack itemStack) {
        super(itemStack);
        if (!itemStack.getType().equals(Material.FIREWORK_STAR)) throw new IllegalArgumentException("This item isn't a firework star!");
    }

    public static FireworkStarItem from(ItemStack itemStack) {
        return new FireworkStarItem(itemStack);
    }

    public static FireworkStarItem newFireworkStar() {
        return new FireworkStarItem();
    }

    public FireworkStarItem withEffect(FireworkEffect.Type type) {
        builder.with(type);
        return this;
    }
    public FireworkStarItem withColors(Color... colors) {
        builder.withColor(colors);
        return this;
    }
    public FireworkStarItem withFadeColors(Color... colors) {
        builder.withFade(colors);
        return this;
    }
    public FireworkStarItem withTrail() {
        builder.trail(true);
        return this;
    }
    public FireworkStarItem withFlicker() {
        builder.flicker(true);
        return this;
    }

    private void applyEffects() {
        FireworkEffectMeta meta = (FireworkEffectMeta) this.itemStack.getItemMeta();
        meta.setEffect(builder.build());
        super.meta(meta);
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
