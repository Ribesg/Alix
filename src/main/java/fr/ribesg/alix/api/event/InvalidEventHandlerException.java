package fr.ribesg.alix.api.event;

import java.lang.reflect.Method;

/**
 * Thrown when a method holding the {@link EventHandler} annotation is
 * malformed.
 */
public class InvalidEventHandlerException extends RuntimeException {

   /**
    * Builds an InvalidEventHandlerException.
    *
    * @param method the method that couldn't support the {@link EventHandler}
    *               annotation.
    * @param reason the reason why the provided method doesn't support the
    *               {@link EventHandler} annotation
    */
   public InvalidEventHandlerException(final Method method, final String reason) {
      super("Method " + method.getDeclaringClass().getName() + '.' + method.getName() + "(...) isn't a valid EventHandler: " + reason);
   }
}
