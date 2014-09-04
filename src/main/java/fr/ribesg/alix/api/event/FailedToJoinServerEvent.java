package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Server;

/**
 * Thrown when {@link fr.ribesg.alix.api.Server#connect()} fails due to an
 * Exception.
 *
 * @author Ribesg
 */
public class FailedToJoinServerEvent extends Event {

   private final Server    server;
   private final Throwable cause;

   public FailedToJoinServerEvent(final Server server, final Throwable cause) {
      this.server = server;
      this.cause = cause;
   }

   public Server getServer() {
      return server;
   }

   public Throwable getCause() {
      return cause;
   }
}
