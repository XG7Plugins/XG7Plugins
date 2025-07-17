package com.xg7plugins.data.config.section;

import com.xg7plugins.boot.Plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation represents a config file.
 * Used to mark classes that represent configuration file structures
 * and specify which configuration file will be used.
 *
 * @see ConfigSection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigFile {

    /**
     * The plugin class that owns this configuration file.
     *
     * @return The plugin class
     */
    Class<? extends Plugin> plugin();

    /**
     * The name of the configuration file.
     *
     * @return The configuration filename
     */
    String configName();

    /**
     * Optional path that represents which path will be used to access
     * the values
     * If not specified, the main section is used.
     *
     * @return The file path, or empty string for the main section
     */
    String path() default "";

}
