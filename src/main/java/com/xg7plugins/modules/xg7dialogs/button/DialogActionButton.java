package com.xg7plugins.modules.xg7dialogs.button;

import com.github.retrooper.packetevents.protocol.chat.clickevent.ShowDialogClickEvent;
import com.github.retrooper.packetevents.protocol.dialog.action.StaticAction;
import com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class DialogActionButton {

    private final String label;
    private final String hoverText;
    private final int width;
    private final DialogAction action;

    public ActionButton build(Dialog dialog, Player player) {
        return new ActionButton(
                new CommonButtonData(
                        Text.detectLangs(player, dialog.getPlugin(), label).join().toAdventureComponent(),
                        Text.detectLangs(player, dialog.getPlugin(), hoverText).join().toAdventureComponent(),
                        width
                ),
                action.build(dialog, player)
        );
    }

}
