package com.xg7plugins.events.bukkitevents;

/**
 * Interface representing a listener that can be enabled or disabled.
 * This provides a base contract for implementing event listeners.
 */
public interface Listener {

    /**
     * Checks if this listener is currently enabled.
     *
     * @return true if the listener is enabled, false otherwise
     */
    boolean isEnabled();

}
