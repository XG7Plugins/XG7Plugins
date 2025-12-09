package com.xg7plugins.modules.xg7dialogs.builder;

import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7dialogs.components.DialogBodyElement;
import com.xg7plugins.modules.xg7dialogs.dialogs.Dialog;
import com.xg7plugins.modules.xg7dialogs.inputs.DialogInput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class DialogBuilder<B extends DialogBuilder<B, R>, R extends Dialog> {

    @NotNull
    protected final Plugin plugin;
    @NotNull
    protected final String id;

    protected String title;
    protected DialogAction afterResponse = DialogAction.CLOSE;
    protected boolean canCloseWithEscape = false;
    protected List<DialogBodyElement> elements = new ArrayList<>();
    protected List<DialogInput>  inputs = new ArrayList<>();

    public B title(String title) {
        this.title = title;
        return (B) this;
    }

    public B afterResponse(DialogAction afterResponse) {
        this.afterResponse = afterResponse;
        return (B) this;
    }

    public B canCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
        return (B) this;
    }

    public B elements(List<DialogBodyElement> elements) {
        this.elements = elements;
        return (B) this;
    }

    public B inputs(List<DialogInput> inputs) {
        this.inputs = inputs;
        return (B) this;
    }

    public B addElement(DialogBodyElement element) {
        elements.add(element);
        return (B) this;
    }
    public B addInput(DialogInput input) {
        inputs.add(input);
        return (B) this;
    }

    public abstract R build();
}
