package com.xg7plugins.modules.xg7dialogs.components.elements;

import com.github.retrooper.packetevents.protocol.dialog.body.*;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
public class PlainMessageElement implements DialogBodyElement<PlainMessageDialogBody> {

    private final String message;
    private final int width;

    @Override
    public DialogBodyType<@NotNull PlainMessageDialogBody> getType() {
        return DialogBodyTypes.PLAIN_MESSAGE;
    }

    @Override
    public PlainMessageDialogBody build(Dialog dialog, Player player) {
        return new PlainMessageDialogBody(new PlainMessage(
                Text.detectLangs(player, dialog.getPlugin(), message)
                        .replaceAll(dialog.getBuildPlaceholders())
                        .toAdventureComponent(),
                width
        ));
    }
}
