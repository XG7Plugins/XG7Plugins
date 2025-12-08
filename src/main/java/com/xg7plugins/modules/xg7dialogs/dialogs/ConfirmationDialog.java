package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogActionButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class ConfirmationDialog extends Dialog {

    private final DialogActionButton yesButton;
    private final DialogActionButton noButton;

    public ConfirmationDialog(Plugin plugin, String id, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, Dialog.ActionType afterResponse, DialogActionButton yesButton, DialogActionButton noButton) {
        super(plugin, id, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse);
        this.yesButton = yesButton;
        this.noButton = noButton;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.ConfirmationDialog(
                buildCommonData(player),
                yesButton.build(this, player),
                noButton.build(this, player)
        );
    }
}
