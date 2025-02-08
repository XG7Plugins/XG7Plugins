package com.xg7plugins.extensions;

import com.xg7plugins.boot.Plugin;

public class ExtensionNotFoundException extends RuntimeException {

    public ExtensionNotFoundException(Plugin plugin, String extensionName) {
        super("This resource requires the extension " + extensionName + " to be loaded in " + plugin.getName());
    }

    public ExtensionNotFoundException(String message) {
        super(message);
    }
}
