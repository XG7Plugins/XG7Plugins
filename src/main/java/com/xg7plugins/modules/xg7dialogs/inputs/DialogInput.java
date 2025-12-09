package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.Input;
import com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;

@Data
@AllArgsConstructor
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

    public static DialogCheckBox booleanInput(String key, String label, boolean defaultValue, String onTrue, String onFalse) {
        return new DialogCheckBox(key, label, defaultValue, onTrue, onFalse);
    }

    public static DialogCheckBox booleanInput(String key, String label, boolean defaultValue) {
        return booleanInput(key, label, defaultValue, "true", "false");
    }

    public static DialogCheckBox booleanInput(String key, String label) {
        return booleanInput(key, label, false, "true", "false");
    }


    public static DialogTextInput textInput(String key, String label, boolean labelVisible, int width, int height, int maxLines, int maxLen, String defValue) {
        return new DialogTextInput(key, label, labelVisible, width, height, maxLines, defValue, maxLen);
    }

    public static DialogTextInput textInput(String key, String label,  boolean labelVisible, int width) {
        return textInput(key, label, labelVisible, width, 16, 1, 32, null);
    }

    public static DialogTextInput textInput(String key, String label, int width) {
        return textInput(key, label, true, width, 16, 1, 32, null);
    }

    public static DialogTextInput textInput(String key, String label, boolean labelVisible) {
        return textInput(key, label, labelVisible, 200);
    }

    public static  DialogTextInput textInput(String key, String label) {
        return textInput(key, label, true);
    }


    public static DialogNumberRange numberRangeInput(String key, String label, String labelFormatter, int width, float start, float end, float steps, float initial) {
        return new DialogNumberRange(key, label, labelFormatter, width, start, end, steps, initial);
    }

    public static DialogNumberRange numberRangeInput(String key, String label, String labelFormatter, float start, float end, float steps) {
        return numberRangeInput(key, label, labelFormatter, 200, start, end, steps, end / 2);
    }
    public static DialogNumberRange numberRangeInput(String key, String label, float start, float end, float steps) {
        return numberRangeInput(key, label, "options.generic_value", start, end, steps);
    }


    public static DialogSingleOption optionsInput(String key, String label, boolean labelVisible, int width, List<DialogSingleOption.Option> options) {
        return new DialogSingleOption(key, label,  labelVisible, width, options);
    }

    public static DialogSingleOption optionsInput(String key, String label, boolean labelVisible, List<DialogSingleOption.Option> options) {
        return optionsInput(key, label, labelVisible, 200, options);
    }

    public static DialogSingleOption optionsInput(String key, String label, List<DialogSingleOption.Option> options) {
        return optionsInput(key, label, true, options);
    }
}
