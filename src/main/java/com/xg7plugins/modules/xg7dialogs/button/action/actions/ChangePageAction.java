package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.chat.clickevent.ChangePageClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

@Data
public class ChangePageAction implements DialogAction {

    private final int page;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new StaticAction(new ChangePageClickEvent(page));
    }
}
