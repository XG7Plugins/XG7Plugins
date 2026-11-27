package com.xg7plugins.api.plugin;

import java.util.Set;

public interface PluginDescription {

    String name();
    String defaultPrefix();
    String description();
    int metricsId();

    String mainCommand();
    String commandPrefixes();

    String asciiArt();

    Set<Collaborator> collaborators();
    Set<BetaTesters> betaTesters();

    interface Collaborator {
        String uuid();
        String name();
        String role();
    }

    interface BetaTesters {
        String uuid();
        String name();
    }
}
