package com.xg7plugins.data.config;

/**
 * Annotation to configure boolean properties.
 * Used to mark fields that represent boolean configuration values
 * and specify how they should be handled in the configuration system.
 */
public @interface ConfigBoolean {

    /**
     * The name of the configuration where this boolean value is stored.
     *
     * @return The configuration name
     */
    String configName();

    /**
     * The path to the boolean value in the configuration.
     *
     * @return The configuration path
     */
    String path();

    /**
     * Whether the boolean value should be inverted when read/written.
     *
     * @return true to invert the value, false to use as-is
     */
    boolean invert() default false;

}
