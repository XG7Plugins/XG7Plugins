package com.xg7plugins.events.packetevents;

import com.github.retrooper.packetevents.event.PacketListenerPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark classes that handle packet events in the plugin system.
 * This annotation configures the priority and type of packets that the handler will process.
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * <code>
 * {@literal @}PacketEventHandler(priority = PacketListenerPriority.HIGH, packet = PacketEventType.ALL)
 * public class MyPacketHandler {
 *     // Handler implementation
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

    /**
     * Defines which type of packets this handler will process.
     *
     * @return The type of packets to handle
     */
    PacketEventType packet() default PacketEventType.ALL;
}
