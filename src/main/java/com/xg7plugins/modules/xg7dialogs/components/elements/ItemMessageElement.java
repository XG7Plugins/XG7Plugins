package com.xg7plugins.modules.xg7dialogs.components.elements;

import com.github.retrooper.packetevents.protocol.dialog.body.DialogBodyType;
import com.github.retrooper.packetevents.protocol.dialog.body.DialogBodyTypes;
import com.github.retrooper.packetevents.protocol.dialog.body.ItemDialogBody;
import com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
public class ItemMessageElement implements DialogBodyElement<ItemDialogBody> {

    private final Item item;
    private final String description;
    private final int textWidth;
    private final boolean showDecorations;
    private final  boolean showTooltip;
    private final int width;
    private final int height;

    @Override
    public DialogBodyType<@NotNull ItemDialogBody> getType() {
        return DialogBodyTypes.ITEM;
    }

    @Override
    public ItemDialogBody build(Dialog dialog, Player player) {
        return new ItemDialogBody(
                item.toProtocolItemStack(player, dialog.getPlugin()),
                new PlainMessage(Text.detectLangs(player, dialog.getPlugin(), description).join().toAdventureComponent(), textWidth),
                showDecorations,
                showTooltip,
                width,
                height
        );
    }
}
