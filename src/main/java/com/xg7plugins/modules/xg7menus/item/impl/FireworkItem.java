package com.xg7plugins.modules.xg7menus.item.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkItem extends Item {

    private FireworkEffect.Builder builder = FireworkEffect.builder();
    private boolean hasColors;

    public FireworkItem() {
        super(new ItemStack(XMaterial.FIREWORK_ROCKET.parseMaterial()));
    }

    public FireworkItem(ItemStack itemStack) {
        super(itemStack);
        if (!itemStack.getType().equals(XMaterial.FIREWORK_ROCKET.parseMaterial())) throw new IllegalArgumentException("This item isn't a firework!");
    }

    public static FireworkItem from(ItemStack itemStack) {
        return new FireworkItem(itemStack);
    }

    public static FireworkItem newFirework() {
        return new FireworkItem();
    }

    public FireworkItem newEffectBuilder() {

        if (!hasColors) builder.withColor(Color.WHITE);

        FireworkEffect effect = builder.build();
        FireworkMeta meta = (FireworkMeta) this.itemStack.getItemMeta();
        meta.addEffect(effect);
        super.meta(meta);

        this.builder = FireworkEffect.builder();

        return this;
    }

    public FireworkItem power(int power) {
        FireworkMeta meta = (FireworkMeta) this.itemStack.getItemMeta();
        meta.setPower(power);
        super.meta(meta);
        return this;
    }
    public FireworkItem withEffect(FireworkEffect.Type type) {
        builder.with(type);
        return this;
    }
    public FireworkItem withColors(Color... colors) {
        hasColors = true;
        builder.withColor(colors);
        return this;
    }
    public FireworkItem withFadeColors(Color... colors) {
        builder.withFade(colors);
        return this;
    }
    public FireworkItem withTrail() {
        builder.trail(true);
        return this;
    }
    public FireworkItem withFlicker() {
        builder.flicker(true);
        return this;
    }

    @Override
    public ItemStack getItemStack() {
        newEffectBuilder();
        return this.itemStack;
    }

    @Override
    public ItemStack getItemFor(CommandSender player, Plugin plugin) {
        newEffectBuilder();
        return super.getItemFor(player, plugin);
    }
}
