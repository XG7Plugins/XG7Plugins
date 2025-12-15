package com.xg7plugins.modules.xg7dialogs.button.action.actions;

import com.github.retrooper.packetevents.protocol.chat.clickevent.OpenUrlClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.Action;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogButtonAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class OpenURLAction implements DialogButtonAction {

    public final String url;

    @Override
    public Action build(Dialog dialog, Player player) {
        return new StaticAction(new OpenUrlClickEvent(
                Text.detectLangs(player, dialog.getPlugin(), url).getText()
        ));
    }
}
