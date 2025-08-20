package com.xg7plugins.events.bukkitevents;

import com.xg7plugins.data.config.section.ConfigVerify;
import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark methods as event handlers in the plugin system.
 * This annotation extends Bukkit's event handling system with additional features
 * such as world-specific event handling and configuration-based enabling/disabling.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * Determines if the event handler should only process events from a specific world.
     *
     * @return true if the event should only be processed for a specific world, false otherwise
     */
    boolean isOnlyInWorld() default false;

    /**
     * Determines if cancelled events should be ignored by this handler.
     *
     * @return true if cancelled events should be ignored, false otherwise
     */
    boolean ignoreCancelled() default false;

    /**
     * Sets the priority level for this event handler.
     *
     * @return the EventPriority level for this handler
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Configures whether this event handler is enabled through configuration settings.
     *
     * @return ConfigVerify annotation containing configuration parameters
     */
    ConfigVerify isEnabled() default @ConfigVerify(
            configName = "",
            path = ""
    );

}
