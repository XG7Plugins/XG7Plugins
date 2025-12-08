package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.chat.clickevent.ShowDialogClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class ShowDialogAction implements DialogAction {

    private final Dialog dialog;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new StaticAction(new ShowDialogClickEvent(dialog.build(player)));
    }
}
