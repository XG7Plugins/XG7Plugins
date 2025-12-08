package com.xg7plugins.modules.xg7dialogs.components;

import com.github.retrooper.packetevents.protocol.dialog.body.*;
import com.xg7plugins.modules.xg7dialogs.components.elements.ItemMessageElement;
import com.xg7plugins.modules.xg7dialogs.components.elements.PlainMessageElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DialogBodyElement<T extends DialogBody> {

    DialogBodyType<@NotNull T> getType();

    T build(Dialog dialog, Player player);

    static PlainMessageElement plainMessage(String message, int width) {
        return new PlainMessageElement(message, width);
    }

    static PlainMessageElement plainMessage(String message) {
        return plainMessage(message, 200);
    }

    static ItemMessageElement item(Item item, String description, int textWidth, boolean showDecorations, boolean showTooltip, int width, int height) {
        return new ItemMessageElement(
                item,
                description,
                textWidth,
                showDecorations,
                showTooltip,
                width,
                height
        );
    }

    static ItemMessageElement item(Item item, String description, int textWidth, boolean showDecorations, boolean showTooltip) {
        return item(item, description, textWidth, showDecorations, showTooltip, 16, 16);
    }

    static ItemMessageElement item(Item item, String description, boolean showDecorations, boolean showTooltip) {
        return item(item, description, 200, showDecorations, showTooltip);
    }

    static ItemMessageElement item(Item item, String description) {
        return item(item, description, true, true);
    }

}
