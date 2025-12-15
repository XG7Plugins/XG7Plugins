package com.xg7plugins.modules.xg7dialogs.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.dialogs.NoticeDialog;

public class NoticeDialogBuilder extends DialogBuilder<NoticeDialogBuilder, NoticeDialog> {

    private DialogButton button;

    public NoticeDialogBuilder(Plugin plugin, String id) {
        super(plugin, id);
    }

    public NoticeDialogBuilder button(DialogButton button) {
        this.button = button;
        return this;
    }

    @Override
    public NoticeDialog build() {

        if (title == null) {
            throw DialogBuildException.noTitle();
        }

        if (button == null) {
            throw new DialogBuildException("This dialog has no \"action button\".");
        }

        return new NoticeDialog(
                plugin,
                id,
                title,
                canCloseWithEscape,
                body,
                inputs,
                afterResponse,
                button,
                placeholders,
                responseHandler
        );
    }
}
