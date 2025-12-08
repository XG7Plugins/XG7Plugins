package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.chat.clickevent.SuggestCommandClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class SuggestCommandAction implements DialogAction {

    private final String command;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new StaticAction(new SuggestCommandClickEvent(
                Text.detectLangs(player, dialog.getPlugin(), command).join().getText()
        ));
    }
}
