package com.xg7plugins.modules.xg7dialogs.inputs;

import com.github.retrooper.packetevents.protocol.dialog.input.InputControl;
import com.github.retrooper.packetevents.protocol.dialog.input.SingleOptionInputControl;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.utils.text.Text;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DialogSingleOption extends DialogInput {

    private final boolean labelVisible;
    private final int width;
    private final List<Option> options;

    public DialogSingleOption(String key, String label, boolean labelVisible, int width, List<Option> options) {
        super(key, label);
        this.labelVisible = labelVisible;
        this.width = width;
        this.options = options;
    }

    @Data
    public static class Option {
        private final String id;
        private final String displayName;
        private final boolean firstSelected;

        public SingleOptionInputControl.Entry build(Dialog dialog, Player player) {
            return new SingleOptionInputControl.Entry(
                    id,
                    Text.detectLangs(player, dialog.getPlugin(), displayName).replaceAll(dialog.getBuildPlaceholders()).toAdventureComponent(),
                    firstSelected
            );
        }

        public static Option of(String id, String displayName, boolean firstSelected) {
            return new Option(id, displayName, firstSelected);
        }

        public static Option of(String id, String displayName) {
            return of(id, displayName, false);
        }
    }

    @Override
    public InputControl buildControl(Dialog dialog, Player player) {
        return new SingleOptionInputControl(
                width,
                options.stream().map(option -> option.build(dialog, player)).collect(Collectors.toList()),
                Text.detectLangs(player, dialog.getPlugin(), this.label).replaceAll(dialog.getBuildPlaceholders()).toAdventureComponent(),
                labelVisible
        );
    }

}
