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
     * Creates an instance of type T from the given configuration section.
     *
     * @param section      The configuration section containing the data to be converted
     * @param optionalArgs Additional arguments that may be needed during the conversion process
     * @return An instance of type T populated with the configuration data
     */
    T fromConfig(Config config, String path, Object... optionalArgs);

    Class<T> getTargetType();

}
