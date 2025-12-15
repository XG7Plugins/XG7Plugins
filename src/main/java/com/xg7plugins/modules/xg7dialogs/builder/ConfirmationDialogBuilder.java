package com.xg7plugins.modules.xg7dialogs.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.dialogs.ConfirmationDialog;

public class ConfirmationDialogBuilder extends DialogBuilder<ConfirmationDialogBuilder, ConfirmationDialog> {

    private DialogButton yesButton;
    private DialogButton noButton;

    public ConfirmationDialogBuilder(Plugin plugin, String id) {
        super(plugin, id);
    }

    public ConfirmationDialogBuilder yesButton(DialogButton yesButton) {
        this.yesButton = yesButton;
        return this;
    }

    public ConfirmationDialogBuilder noButton(DialogButton noButton) {
        this.noButton = noButton;
        return this;
    }

    @Override
    public ConfirmationDialog build() {

        if (title == null) {
            throw DialogBuildException.noTitle();
        }

        if (yesButton == null || noButton == null) {
            throw new DialogBuildException("This dialog has no \"yes button\" or \"no button\".");
        }

        return new ConfirmationDialog(
                plugin,
                id,
                title,
                canCloseWithEscape,
                body,
                inputs,
                afterResponse,
                yesButton,
                noButton,
                placeholders,
                responseHandler
        );
    }
}
