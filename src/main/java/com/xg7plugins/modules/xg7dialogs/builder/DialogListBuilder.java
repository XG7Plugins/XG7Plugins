package com.xg7plugins.modules.xg7dialogs.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.button.DialogButton;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.modules.xg7dialogs.dialogs.DialogList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogListBuilder extends DialogBuilder<DialogListBuilder, DialogList> {

    private final List<Dialog> dialogs = new ArrayList<>();

    private DialogButton exitButton;
    private int buttonWidth = 150;
    private int columns = 2;

    public DialogListBuilder(@NotNull Plugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    public DialogListBuilder exitButton(@NotNull DialogButton exitButton) {
        this.exitButton = exitButton;
        return this;
    }

    public DialogListBuilder buttonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
        return this;
    }

    public DialogListBuilder columns(int columns) {
        this.columns = columns;
        return this;
    }

    @Override
    public DialogList build() {

        if (dialogs.isEmpty()) {
            throw new DialogBuildException("This dialog must have at least one dialog");
        }

        if (exitButton == null) {
            throw new DialogBuildException("ExitButton must not be null");
        }

        if (title == null) {
            throw DialogBuildException.noTitle();
        }

        return new DialogList(
                plugin,
                id,
                title,
                canCloseWithEscape,
                elements,
                inputs,
                afterResponse,
                exitButton,
                dialogs,
                columns,
                buttonWidth
        );
    }
}
