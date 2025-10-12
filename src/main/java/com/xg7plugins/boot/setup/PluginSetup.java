package com.xg7plugins.boot.setup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to configure plugin setup parameters.
 * This annotation is processed to initialize plugin settings.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginSetup {

    /**
     * The prefix used for plugin messages and commands
     */
    String prefix();

    /**
     * The main command name for the plugin
     */
    String mainCommandName();

    /**
     * Alternative names/aliases for the main command
     */
    String[] mainCommandAliases();

    /**
     * Configuration files to be loaded
     */
    String[] configs() default {};

    /**
     * Messages to be drawn when the plugin is enabled
     */
    String[] onEnableDraw() default {};

    /**
     * Events that trigger plugin reload
     */
    String[] reloadCauses() default {};

    Collaborator[] collaborators() default {@Collaborator(uuid = "45766b7f-9789-40e1-bd0b-46fa0d032bde", name = "&aDaviXG7", role = "&bCreator of all plugin")};


}
