package com.xg7plugins.modules.xg7dialogs.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.dialogs.MultiActionDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiActionDialogBuilder extends DialogBuilder<MultiActionDialogBuilder, MultiActionDialog> {

    private int columns = 2;
    private List<DialogButton> actionButtons = new ArrayList<>();
    private DialogButton exitButton;

    public MultiActionDialogBuilder(@NotNull Plugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    public MultiActionDialogBuilder columns(int columns) {
        this.columns = columns;
        return this;
    }

    public MultiActionDialogBuilder actionButtons(List<DialogButton> actionButtons) {
        this.actionButtons = actionButtons;
        return this;
    }

    public MultiActionDialogBuilder actionButtons(DialogButton... actionButtons) {
        this.actionButtons = Arrays.asList(actionButtons);
        return this;
    }

    public MultiActionDialogBuilder exitButton(@NotNull DialogButton exitButton) {
        this.exitButton = exitButton;
        return this;
    }


    @Override
    public MultiActionDialog build() {

        if (title == null) {
            throw DialogBuildException.noTitle();
        }

        if (exitButton == null) {
            throw new DialogBuildException("Exit button is null");
        }

        return new MultiActionDialog(
                plugin,
                id,
                title,
                canCloseWithEscape,
                elements,
                inputs,
                afterResponse,
                columns,
                actionButtons,
                exitButton
        );
    }
}
