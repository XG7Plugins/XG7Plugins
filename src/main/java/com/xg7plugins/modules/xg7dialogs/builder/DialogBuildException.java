package com.xg7plugins.modules.xg7dialogs.builder;

public class DialogBuildException extends NullPointerException {

    public DialogBuildException(String message) {
        super(message);
    }

    public static DialogBuildException noTitle() {
        return new DialogBuildException("There is no title");
    }
}
