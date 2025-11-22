package com.xg7plugins.utils.item.parser.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.item.impl.SpawnerItem;
import com.xg7plugins.utils.item.parser.ItemParser;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class SpawnerParser implements ItemParser<SpawnerItem> {
    @Override
    public List<XMaterial> getMaterials() {
        return Collections.singletonList(XMaterial.SPAWNER);
    }

    @Override
    public SpawnerItem parse(XMaterial material, String... args) {

        if (args.length == 0) return SpawnerItem.newSpawner();

        SpawnerItem item = SpawnerItem.newSpawner();

        String entity = args[0];

        try {
            return item.entityId(Parser.INTEGER.convert(entity));
        } catch (Exception ig) {
            try {
                return item.entity(EntityType.valueOf(entity.toUpperCase()));
            } catch (Exception ig2) {
                return SpawnerItem.newSpawner().entity(EntityType.PIG);
            }
        }


    }
}
