package com.xg7plugins.utils.text.newComponent.serializer.tags.modifiers;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.modfiers.color.HexModifier;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TagType;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TextTag;

import java.util.List;

public class HexTag implements TextTag {
    @Override
    public String name() {
        return "hex";
    }

    @Override
    public TagType getType() {
        return TagType.MODIFIER;
    }

    @Override
    public void resolve(Component component, List<String> openArgs, List<String> closeArgs) {
        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Hex tag must have 1 open arguments");
        }

        HexModifier hexModifier = HexModifier.of(openArgs.get(0));

        component.addModifier(hexModifier);
    }
}
