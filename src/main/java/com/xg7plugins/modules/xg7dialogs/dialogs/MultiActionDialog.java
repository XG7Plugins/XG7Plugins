package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogActionButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MultiActionDialog extends Dialog {

    private final int columns;
    private final List<DialogActionButton> actionButtons;
    private final DialogActionButton exitButton;

    public MultiActionDialog(Plugin plugin, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, ActionType afterResponse, int columns, List<DialogActionButton> actionButtons, DialogActionButton exitButton) {
        super(plugin, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse);
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

    @Override
    public void onResponse() {

    }
}
