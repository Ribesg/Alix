package fr.ribesg.alix.api.event;

/**
 * Thrown when {@link fr.ribesg.alix.api.Server#connect()} fails due to an
 * Exception.
 *
 * @author Ribesg
 */
public class FailedToJoinServerEvent extends Event {

   private final Throwable cause;

   public FailedToJoinServerEvent(final Throwable cause) {
      this.cause = cause;
   }

   public Throwable getCause() {
      return cause;
   }
}
