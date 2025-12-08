package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.Input;
import com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public abstract class DialogInput {

    protected final String key;
    protected final String label;

    public Input build(Dialog dialog, Player player) {
        return new Input(
                this.key,
                buildControl(dialog, player)
        );
    }
    public abstract InputControl buildControl(Dialog dialog, Player player);

}
