package com.xg7plugins.temp.xg7menus;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MenuException extends RuntimeException {
    public MenuException(ExceptionCause cause, String message) {
        super(cause.name() + " " + cause.message + ": " + message);
    }

    @AllArgsConstructor
    @Getter
    public enum ExceptionCause {
        BUILD_ERROR("An error occurred while building the menu"),
        SLOT_OUT_OF_BOUNDS("Invalid slot");

        private final String message;
    }
}
