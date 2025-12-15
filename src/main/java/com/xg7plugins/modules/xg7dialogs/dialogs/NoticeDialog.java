package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import com.xg7plugins.utils.Pair;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class NoticeDialog extends Dialog {

    private final DialogButton actionButton;

    public NoticeDialog(Plugin plugin, String id, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, DialogAction afterResponse, DialogButton actionButton, List<Pair<String, String>> buildPlaceholders, DialogResponseHandler responseHandler) {
        super(plugin, id, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse, buildPlaceholders, responseHandler);
        this.actionButton = actionButton;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.NoticeDialog(
                buildCommonData(player),
                actionButton.build(this, player)
        );
    }

}
