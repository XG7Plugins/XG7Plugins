package com.xg7plugins.utils.text.component.deserializer.tags.modifiers;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.modfiers.color.HexModifier;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;

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
    public void resolve(Component component, List<String> args) {
        if (MinecraftVersion.isOlderThan(16)) return;

        if (args.size() != 1) {
            throw new IllegalArgumentException("Hex tag must have 1 arguments");
        }

        HexModifier hexModifier = HexModifier.of(args.get(0));

        component.addModifier(hexModifier);
    }
}
