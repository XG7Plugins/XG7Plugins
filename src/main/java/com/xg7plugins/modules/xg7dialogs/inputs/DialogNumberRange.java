package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.NumberRangeInputControl;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class DialogNumberRange extends DialogInput {

    private final String labelFormatter;
    private final int width;
    private final float start;
    private final float end;
    private final float steps;
    private final float initial;

    public DialogNumberRange(String key, String label, String labelFormatter, int width, float start, float end, float steps, float initial) {
        super(key, label);
        this.labelFormatter = labelFormatter;
        this.width = width;
        this.start = start;
        this.end = end;
        this.steps = steps;
        this.initial = initial;
    }

    @Override
    public InputControl buildControl(Dialog dialog, Player player) {
        return new NumberRangeInputControl(
                width,
                Text.detectLangs(player, dialog.getPlugin(), this.label).join().toAdventureComponent(),
                labelFormatter,
                new NumberRangeInputControl.RangeInfo(
                        start,
                        end,
                        initial,
                        steps
                )
        );
    }
}
