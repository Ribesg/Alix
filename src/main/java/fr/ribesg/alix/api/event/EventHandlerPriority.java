package fr.ribesg.alix.api.event;

/**
 * Defines the priority of an EventHandler.
 */
public enum EventHandlerPriority {

   /**
    * Use this priority if you want to handle an event before any stock
    * handler.
    */
   HIGH,

   /**
    * The INTERNAL HandlerPriority is used by any stock Handler.
    * <p>
    * <strong>You should not use this priority!</strong>
    */
   @Deprecated INTERNAL,

   /**
    * Use this priority if you want to handle an event after any stock
    * handler.
    * <p>
    * This is the default priority.
    */
   LOW,
}
