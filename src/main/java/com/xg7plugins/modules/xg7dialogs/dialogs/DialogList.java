package com.xg7plugins.modules.xg7dialogs.dialogs;

import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.github.retrooper.packetevents.protocol.dialog.DialogListDialog;
import com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DialogList extends Dialog {

    private final DialogButton exitButton;
    private final List<Dialog> dialogs;
    private final int columns;
    private final int buttonWidth;

    public DialogList(Plugin plugin, String id, String title, boolean canCloseWithEscape, List<DialogBodyElement> dialogBodyElements, List<DialogInput> dialogInputs, DialogAction afterResponse, DialogButton exitButton, List<Dialog> dialogs, int columns, int buttonWidth) {
        super(plugin, id, title, canCloseWithEscape, dialogBodyElements, dialogInputs, afterResponse);
        this.exitButton = exitButton;
        this.dialogs = dialogs;
        this.columns = columns;
        this.buttonWidth = buttonWidth;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.dialog.Dialog build(Player player) {
        return new DialogListDialog(
                buildCommonData(player),
                dialogs == null || dialogs.isEmpty() ? MappedEntitySet.createEmpty() :
                        new MappedEntitySet<>(dialogs.stream().map(dialog -> dialog.build(player)).collect(Collectors.toList())),
                exitButton.build(this, player),
                columns,
                buttonWidth
        );
    }
}
