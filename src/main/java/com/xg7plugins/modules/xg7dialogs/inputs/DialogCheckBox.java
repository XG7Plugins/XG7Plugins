package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.*;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class DialogCheckBox extends DialogInput {

    private final boolean initialValue;
    private final String onTrue;
    private final String onFalse;

    public DialogCheckBox(String key, String label, boolean initialValue, String onTrue, String onFalse) {
        super(key, label);
        this.initialValue = initialValue;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    @Override
    public InputControl buildControl(Dialog dialog, Player player) {
        return new BooleanInputControl(
                Text.detectLangs(player, dialog.getPlugin(), this.label).replaceAll(dialog.getBuildPlaceholders()).getComponent(),
                initialValue,
                Text.detectLangs(player, dialog.getPlugin(), this.onTrue).replaceAll(dialog.getBuildPlaceholders()).getText(),
                Text.detectLangs(player, dialog.getPlugin(), this.onFalse).replaceAll(dialog.getBuildPlaceholders()).getText()
        );
    }
}
