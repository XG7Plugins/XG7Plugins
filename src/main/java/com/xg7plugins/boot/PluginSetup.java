package com.xg7plugins.boot;

import com.xg7plugins.data.config.section.ConfigSection;

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
     * Separated config sections to be loaded for easier access
     */
    Class<? extends ConfigSection>[] configSections() default {};

    /**
     * Messages to be drawn when the plugin is enabled
     */
    String[] onEnableDraw() default {};

    /**
     * Events that trigger plugin reload
     */
    String[] reloadCauses() default {};


}
