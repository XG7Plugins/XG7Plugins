package com.xg7plugins.modules.xg7menus.item.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class SpawnerItem extends Item {
    public SpawnerItem() {
        super(Item.from(XMaterial.SPAWNER).getItemStack());
    }
    public SpawnerItem(ItemStack spawner) {
        super(spawner);
        if (!spawner.getType().equals(Material.SPAWNER)) throw new IllegalArgumentException("This item isn't a spawner!");
    }

    public static SpawnerItem from(ItemStack itemStack) {
        return new SpawnerItem(itemStack);
    }

    public static SpawnerItem newSpawner() {
        return new SpawnerItem();
    }

    public SpawnerItem entityId(int entityId) {
        BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner spawnerState = (CreatureSpawner) meta.getBlockState();
        spawnerState.setSpawnedType(EntityType.fromId(entityId));
        super.meta(meta);

        return this;
    }

    public SpawnerItem entity(EntityType type) {
        BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner spawnerState = (CreatureSpawner) meta.getBlockState();
        spawnerState.setSpawnedType(type);

        super.meta(meta);

        return this;
    }
}
