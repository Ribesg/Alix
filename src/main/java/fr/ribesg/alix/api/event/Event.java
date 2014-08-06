package fr.ribesg.alix.api.event;

/**
 * Represents an Event.
 * <p>
 * An Event can be consumed by an EventHandler.
 *
 * TODO Document internal behaviour for all Events
 */
public class Event {

   /**
    * Consumption state of this Event
    */
   private boolean consumed;

   /**
    * Creates an Event.
    */
   protected Event() {
      this.consumed = false;
   }

   /**
    * Checks if this Event has been consumed by an EventHandler.
    *
    * @return return if this Event has been consumed by an EventHandler,
    * false otherwise
    */
   public boolean isConsumed() {
      return this.consumed;
   }

   /**
    * Consumes this Event.
    */
   public void consume() {
      this.consumed = true;
   }
}
