package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Log;
import org.jsoup.helper.Validate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages Events. Yeah.
 */
public class EventManager {

   /**
    * This little private Class is used to be able to store Object instances
    * and associated EventHandler methods in pairs.
    */
   private class ObjectMethod {

      /**
       * Object instance
       */
      public final Object instance;

      /**
       * EventHandler method
       */
      public final Method method;

      /**
       * Builds an ObjectMethod pair.
       *
       * @param instance an Object instance
       * @param method   a Method declared by the instance's Class
       */
      private ObjectMethod(final Object instance, final Method method) {
         this.instance = instance;
         this.method = method;
      }
   }

   /**
    * The EventManager instance
    */
   private static final EventManager instance = new EventManager();

   /**
    * Gets the EventManager instance.
    *
    * @return the EventManager instance
    */
   public static EventManager getInstance() {
      return EventManager.instance;
   }

   /**
    * A Map to store all the EventHandlers
    */
   private final Map<Class<? extends Event>, Map<HandlerPriority, List<ObjectMethod>>> handlers;

   /**
    * Builds the EventManager instance
    */
   private EventManager() {
      this.handlers = new HashMap<>();
   }

   /**
    * Registers all handlers of the provided object.
    * <p>
    * Valid EventHandlers are public method with the {@link EventHandler}
    * annotation.
    *
    * @param handlersHolder an object holding one or multiple EventHandlers
    */
   @SuppressWarnings("unchecked SuspiciousMethodCalls")
   public void registerHandlers(final Object handlersHolder) {
      Validate.notNull(handlersHolder, "handlersHolder can't be null");
      EventHandler eh;
      Class<?> parameterType;
      boolean handlerRegistered = false;
      for (final Method m : handlersHolder.getClass().getMethods()) {
         if ((eh = m.getAnnotation(EventHandler.class)) != null) {
            if (m.getParameterCount() != 1 || !Event.class.isAssignableFrom((parameterType = m.getParameterTypes()[0]))) {
               throw new InvalidEventHandlerException(m, "Invalid parameter count or type");
            } else if (!m.isAccessible()) {
               throw new InvalidEventHandlerException(m, "Not public");
            } else {
               Map<HandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(parameterType);
               if (eventHandlers == null) {
                  eventHandlers = new EnumMap<>(HandlerPriority.class);
                  this.handlers.put((Class<? extends Event>) parameterType, eventHandlers);
               }
               List<ObjectMethod> priorityHandlers = eventHandlers.get(eh.priority());
               if (priorityHandlers == null) {
                  priorityHandlers = new ArrayList<>();
                  eventHandlers.put(eh.priority(), priorityHandlers);
               }
               priorityHandlers.add(new ObjectMethod(handlersHolder, m));
               handlerRegistered = true;
            }
         }
      }
      if (!handlerRegistered) {
         throw new IllegalArgumentException("Provided object class '" + handlersHolder.getClass().getName() + "' has no valid EventHandler");
      }
   }

   /**
    * Unregisters all handlers of the provided object.
    *
    * @param handlersHolder an object holding one or multiple registered
    *                       EventHandlers
    */
   public void unRegisterHandlers(final Object handlersHolder) {
      this.unRegisterHandlers(handlersHolder, false);
   }

   /**
    * Unregisters all handlers of the provided object.
    *
    * @param handlersHolder     an object holding one or multiple registered
    *                           EventHandlers
    * @param ignoreUnregistered if this call should ignore errors that may
    *                           occur if the provided instance isn't
    *                           registered
    */
   @SuppressWarnings("SuspiciousMethodCalls")
   public void unRegisterHandlers(final Object handlersHolder, final boolean ignoreUnregistered) {
      Validate.notNull(handlersHolder, "handlersHolder can't be null");
      EventHandler eh;
      Class<?> parameterType;
      boolean registeredHandlerFound = ignoreUnregistered;
      for (final Method m : handlersHolder.getClass().getMethods()) {
         if (m.isAccessible() && (eh = m.getAnnotation(EventHandler.class)) != null) {
            if (m.getParameterCount() != 1 || !Event.class.isAssignableFrom((parameterType = m.getParameterTypes()[0]))) {
               throw new InvalidEventHandlerException(m, "Invalid parameter count or type");
            } else {
               Map<HandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(parameterType);
               if (eventHandlers != null) {
                  List<ObjectMethod> priorityHandlers = eventHandlers.get(eh.priority());
                  if (priorityHandlers != null) {
                     final Iterator<ObjectMethod> it = priorityHandlers.iterator();
                     while (it.hasNext()) {
                        if (it.next().instance == handlersHolder) {
                           it.remove();
                           registeredHandlerFound = true;
                        }
                     }
                     if (priorityHandlers.isEmpty()) {
                        eventHandlers.remove(eh.priority());
                     }
                  }
                  if (eventHandlers.isEmpty()) {
                     this.handlers.remove(parameterType);
                  }
               }
            }
         }
      }
      if (!registeredHandlerFound) {
         throw new IllegalArgumentException("Provided instance of '" + handlersHolder.getClass().getName() + "' has no registered EventHandler");
      }
   }

   /**
    * Calls an Event.
    *
    * @param event an Event
    */
   public void call(final Event event) {
      final Class<? extends Event> clazz = event.getClass();
      final Map<HandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(clazz);
      if (eventHandlers != null) {
         for (final HandlerPriority priority : HandlerPriority.values()) {
            final List<ObjectMethod> priorityHandlers = eventHandlers.get(priority);
            if (priorityHandlers != null) {
               for (final ObjectMethod om : priorityHandlers) {
                  try {
                     om.method.invoke(om.instance, event);
                  } catch (final Throwable t) {
                     Log.error("EventHandler '" + om.method.getDeclaringClass().getName() + '.' + om.method.getName() + "(...)' invokation failed: " + t.getMessage());
                  }
               }
            }
         }
      }
   }
}
