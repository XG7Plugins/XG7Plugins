package com.xg7plugins.data.config;

/**
 * An adapter interface for converting Bukkit configuration sections into typed objects.
 * Provides a standardized way to deserialize configuration data into specific object types,
 * allowing for type-safe configuration handling in the application.
 *
 * @param <T> The type of object that will be created from the configuration data
 */
public interface ConfigTypeAdapter<T> {

    /**
     * Creates an instance of type T from the provided configuration.
     *
     * @param config       The configuration object containing the data
     * @param path         The path where the data is located in the configuration
     * @param optionalArgs Optional arguments that may be needed during the conversion
     * @return An instance of type T created from the configuration data
     */
    T fromConfig(Config config, String path, Object... optionalArgs);

    /**
     * Gets the target class type that this adapter converts to.
     *
     * @return The Class object representing the target type T
     */
    Class<T> getTargetType();

}
