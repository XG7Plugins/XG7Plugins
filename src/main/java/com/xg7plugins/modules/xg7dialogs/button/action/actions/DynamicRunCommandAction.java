package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.DialogTemplate;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class DynamicRunCommandAction implements DialogAction {

    private final String command;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new com.github.retrooper.packetevents.protocol.dialog.action.DynamicRunCommandAction(
                new DialogTemplate(Text.detectLangs(player, dialog.getPlugin(), command).join().getText())
        );
    }
}
