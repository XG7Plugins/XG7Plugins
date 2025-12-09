package com.xg7plugins.modules.xg7dialogs.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.dialogs.ServerLinksDialog;
import org.jetbrains.annotations.NotNull;

public class ServerLinksDialogBuilder extends DialogBuilder<ServerLinksDialogBuilder, ServerLinksDialog> {

    private DialogButton exitButton;
    private int columns = 2;
    private int buttonWidth = 150;

    public ServerLinksDialogBuilder(@NotNull Plugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    public ServerLinksDialogBuilder exitButton(@NotNull DialogButton button) {
        this.exitButton = button;
        return this;
    }

    public ServerLinksDialogBuilder columns(int columns) {
        this.columns = columns;
        return this;
    }

    public ServerLinksDialogBuilder buttonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
        return this;
    }

    @Override
    public ServerLinksDialog build() {

        if (title == null) {
            throw DialogBuildException.noTitle();
        }

        if (exitButton == null) {
            throw new DialogBuildException("Exit button is null");
        }

        return new ServerLinksDialog(
                plugin,
                id,
                title,
                canCloseWithEscape,
                elements,
                inputs,
                afterResponse,
                exitButton,
                columns,
                buttonWidth
        );
    }
}
