package com.xg7plugins.modules.xg7dialogs.button;

import com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import com.xg7plugins.modules.xg7dialogs.button.action.DialogButtonAction;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class DialogButton {

    private final String label;
    private final String hoverText;
    private final int width;
    private final DialogButtonAction action;

    public ActionButton build(Dialog dialog, Player player) {
        return new ActionButton(
                new CommonButtonData(
                        Text.detectLangs(player, dialog.getPlugin(), label).join().toAdventureComponent(),
                        hoverText == null ? Component.text("") : Text.detectLangs(player, dialog.getPlugin(), hoverText).join().toAdventureComponent(),
                        width
                ),
                action == null? null : action.build(dialog, player)
        );
    }

    public static DialogButton newButton(String label, String hoverText, int width, DialogButtonAction action) {
        return new DialogButton(label, hoverText, width, action);
    }

    public static DialogButton newButton(String label, String hoverText, int width) {
        return newButton(label, hoverText, width, null);
    }

    public static DialogButton newButton(String label, String hoverText) {
        return newButton(label, hoverText, 150);
    }

    public static DialogButton newButton(String label) {
        return new DialogButton(label, null, 150, null);
    }

    public static DialogButton newButton(String label, int width) {
        return newButton(label, null, width, null);
    }

    public static DialogButton newButton(String label, String hoverText, DialogButtonAction action) {
        return newButton(label, hoverText, 150, action);
    }

    public static DialogButton newButton(String label, DialogButtonAction action) {
        return newButton(label, null, 150, action);
    }

}
