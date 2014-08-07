package fr.ribesg.alix.api;

import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.event.Event;
import fr.ribesg.alix.api.event.EventHandler;
import fr.ribesg.alix.api.event.EventHandlerPriority;
import fr.ribesg.alix.api.event.InvalidEventHandlerException;
import fr.ribesg.alix.api.event.ReceivedPacketEvent;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;
import org.jsoup.helper.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
   private final Map<Class<? extends Event>, Map<EventHandlerPriority, List<ObjectMethod>>> handlers;

   /**
    * {@link Callback#onReceivedPacket(fr.ribesg.alix.api.event.ReceivedPacketEvent)}
    */
   private final Method callbackHandler;

   /**
    * Builds the EventManager instance
    */
   private EventManager() {
      this.handlers = new ConcurrentHashMap<>();
      try {
         this.callbackHandler = Callback.class.getDeclaredMethod("onReceivedPacket", ReceivedPacketEvent.class);
      } catch (final NoSuchMethodException e) {
         Log.error("Failed to initialize EventManager.callbackHandler", e);
         throw new RuntimeException("Failed to initialize EventManager.callbackHandler", e);
      }

      new AbstractRepeatingThread("Cb-Cleaner", 2_500) {

         @Override
         protected void work() throws InterruptedException {
            EventManager.this.cleanCallbacks();
         }
      }.start();
   }

   /**
    * Iterates over all handlers and check timeout states of Callback.
    *
    * FIXME Callbacks timeout could be handled better than that...
    */
   private void cleanCallbacks() {
      final Iterator<Entry<Class<? extends Event>, Map<EventHandlerPriority, List<ObjectMethod>>>> it1 = this.handlers.entrySet().iterator();
      while (it1.hasNext()) {
         final Entry<Class<? extends Event>, Map<EventHandlerPriority, List<ObjectMethod>>> e1 = it1.next();
         final Iterator<Entry<EventHandlerPriority, List<ObjectMethod>>> it2 = e1.getValue().entrySet().iterator();
         while (it2.hasNext()) {
            final Entry<EventHandlerPriority, List<ObjectMethod>> e2 = it2.next();
            final Iterator<ObjectMethod> it3 = e2.getValue().iterator();
            while (it3.hasNext()) {
               final ObjectMethod om = it3.next();
               if (om.method == this.callbackHandler) {
                  final Callback callback = (Callback) om.instance;
                  if (((Callback) om.instance).getTimeoutDate() > System.currentTimeMillis()) {
                     try {
                        callback.onTimeout();
                     } catch (final Throwable t) {
                        Log.error("Callback onTimeout call threw an error: " + t.getMessage(), t);
                     }
                     it3.remove();
                  }
               }
            }
            if (e2.getValue().isEmpty()) {
               it2.remove();
            }
         }
         if (e1.getValue().isEmpty()) {
            it1.remove();
         }
      }
   }

   /**
    * Registers all handlers of the provided object.
    * <p>
    * Valid EventHandlers are public method with the {@link fr.ribesg.alix.api.event.EventHandler}
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
            } else if (!Modifier.isPublic(m.getModifiers())) {
               throw new InvalidEventHandlerException(m, "Not public");
            } else {
               Map<EventHandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(parameterType);
               if (eventHandlers == null) {
                  eventHandlers = new ConcurrentHashMap<>();
                  this.handlers.put((Class<? extends Event>) parameterType, eventHandlers);
               }
               List<ObjectMethod> priorityHandlers = eventHandlers.get(eh.priority());
               if (priorityHandlers == null) {
                  priorityHandlers = new CopyOnWriteArrayList<>();
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
               Map<EventHandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(parameterType);
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
    * Registers a Callback.
    * <p>
    * Note: Callbacks are automatically registered and unregistered, you
    * do not need to call that method.
    *
    * @param callback the Callback
    */
   public void registerCallback(final Callback callback) {
      Validate.notNull(callback, "callback can't be null");
      Map<EventHandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(ReceivedPacketEvent.class);
      if (eventHandlers == null) {
         eventHandlers = new ConcurrentHashMap<>();
         this.handlers.put(ReceivedPacketEvent.class, eventHandlers);
      }
      List<ObjectMethod> priorityHandlers = eventHandlers.get(callback.getPriority());
      if (priorityHandlers == null) {
         priorityHandlers = new CopyOnWriteArrayList<>();
         eventHandlers.put(callback.getPriority(), priorityHandlers);
      }
      priorityHandlers.add(new ObjectMethod(callback, this.callbackHandler));
   }

   /**
    * Unregisters a Callback.
    * <p>
    * Note: Callbacks are automatically registered and unregistered, you
    * do not need to call that method.
    *
    * @param callback the Callback
    */
   public void unregisterCallback(final Callback callback) {
      Validate.notNull(callback, "callback can't be null");
      Map<EventHandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(ReceivedPacketEvent.class);
      if (eventHandlers != null) {
         List<ObjectMethod> priorityHandlers = eventHandlers.get(callback.getPriority());
         if (priorityHandlers != null) {
            final Iterator<ObjectMethod> it = priorityHandlers.iterator();
            while (it.hasNext()) {
               if (it.next().instance == callback) {
                  it.remove();
               }
            }
            if (priorityHandlers.isEmpty()) {
               eventHandlers.remove(callback.getPriority());
            }
         }
         if (eventHandlers.isEmpty()) {
            this.handlers.remove(ReceivedPacketEvent.class);
         }
      }
   }

   /**
    * Calls an Event.
    *
    * @param event an Event
    */
   public void call(final Event event) {
      Log.debug("Handling event " + event);
      final Class<? extends Event> clazz = event.getClass();
      final Map<EventHandlerPriority, List<ObjectMethod>> eventHandlers = this.handlers.get(clazz);
      if (eventHandlers != null) {
         for (final EventHandlerPriority priority : EventHandlerPriority.values()) {
            final List<ObjectMethod> priorityHandlers = eventHandlers.get(priority);
            if (priorityHandlers != null) {
               final Iterator<ObjectMethod> it = priorityHandlers.iterator();
               while (it.hasNext()) {
                  final ObjectMethod om = it.next();
                  if (om.method == this.callbackHandler) {
                     final Callback callback = (Callback) om.instance;
                     final ReceivedPacketEvent packetEvent = (ReceivedPacketEvent) event;
                     if (callback.listensTo(packetEvent.getPacket().getRawCommandString())) {
                        try {
                           if ((boolean) this.callbackHandler.invoke(callback, packetEvent)) {
                              it.remove();
                           }
                        } catch (final Throwable t) {
                           Log.error("Callback invokation failed on packet '" + packetEvent.getPacket().toString() + "': " + t.getMessage());
                           it.remove();
                        }
                     }
                  } else {
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

      Log.debug("Event " + (event.isConsumed() ? "not" : "") + " consumed: " + event);
   }
}
