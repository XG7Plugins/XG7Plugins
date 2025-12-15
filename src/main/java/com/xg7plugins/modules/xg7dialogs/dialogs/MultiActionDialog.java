package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import com.xg7plugins.utils.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MultiActionDialog extends Dialog {

    private final int columns;
    private final List<DialogButton> actionButtons;
    private final DialogButton exitButton;

    public MultiActionDialog(Plugin plugin, String id, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, DialogAction afterResponse, int columns, List<DialogButton> actionButtons, DialogButton exitButton, List<Pair<String, String>> buildPlaceholders, DialogResponseHandler responseHandler) {
        super(plugin, id, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse, buildPlaceholders, responseHandler);
        this.columns = columns;
        this.actionButtons = actionButtons;
        this.exitButton = exitButton;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog(
                buildCommonData(player),
                actionButtons.stream().map(actionButton -> actionButton.build(this, player)).collect(Collectors.toList()),
                exitButton.build(this, player),
                columns
        );
    }
}
