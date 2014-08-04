package fr.ribesg.alix.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to state that a method is an Event Handler.
 * <p>
 * Any class containing at least an Event Handler should register itself to
 * the {@link EventManager} using
 * {@link EventManager#registerHandlers(Object)}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

   /**
    * Priority of this EventHandler.
    * <p>
    * Default: {@link HandlerPriority#LOW}
    *
    * @see HandlerPriority
    */
   HandlerPriority priority() default HandlerPriority.LOW;

   /**
    * States if this EventHandler wants to ignore Events which have already
    * been consumed by another EventHandler or not.
    * <p>
    * Default: true
    */
   boolean ignoreConsumed() default true;
}
