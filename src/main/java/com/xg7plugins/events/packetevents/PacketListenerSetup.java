package com.xg7plugins.events.packetevents;

import com.github.retrooper.packetevents.event.PacketListenerPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark classes that sets the priority for packet event handlers.
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * <code>
 * {@literal @}PacketEventHandler(priority = PacketListenerPriority.HIGH)
 * public class MyPacketHandler implements PacketListener {
 *     // Handler implementation
 *
 *     Set<PacketTypeCommon> getHandledEvents();
 * }
 * </code>
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketListenerSetup {
    /**
     * Specifies the priority level for this packet event handler.
     * Higher priority handlers are executed before lower priority ones.
     *
     * @return The priority level for this handler
     */
    PacketListenerPriority priority() default PacketListenerPriority.NORMAL;
}
