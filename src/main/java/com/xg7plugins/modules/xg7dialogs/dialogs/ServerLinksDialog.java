package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogActionButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import org.bukkit.entity.Player;

import java.util.List;

public class ServerLinksDialog extends Dialog {

    private final DialogActionButton exitButton;
    private final int columns;
    private final int buttonWidth;

    public ServerLinksDialog(Plugin plugin, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, ActionType afterResponse, DialogActionButton exitButton, int columns, int buttonWidth) {
        super(plugin, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse);
        this.exitButton = exitButton;
        this.columns = columns;
        this.buttonWidth = buttonWidth;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.ServerLinksDialog(
                buildCommonData(player),
                exitButton.build(this, player),
                columns,
                buttonWidth
        );
    }

    @Override
    public void onResponse() {

    }
}
