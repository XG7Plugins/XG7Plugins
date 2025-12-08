package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class DialogTextInput extends DialogInput {

    private final boolean labelVisible;
    private final int height;
    private final int width;
    private final int lines;
    private final int maxLines;
    private final String initialValue;
    private final int maxLength;


    public DialogTextInput(String key, String label, boolean labelVisible, int width, int height, int lines, int maxLines, String initialValue, int maxLength) {
        super(key, label);
        this.labelVisible = labelVisible;
        this.width = width;
        this.height = height;
        this.lines = lines;
        this.maxLines = maxLines;
        this.initialValue = initialValue;
        this.maxLength = maxLength;
    }

    @Override
    public InputControl buildControl(Dialog dialog, Player player) {
        return new TextInputControl(
                width,
                Text.detectLangs(player, dialog.getPlugin(), this.label).join().toAdventureComponent(),
                labelVisible,
                initialValue,
                maxLength,
                new TextInputControl.MultilineOptions(
                        maxLines,
                        height
                )
        );
    }
}
