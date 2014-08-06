package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Server;

/**
 * Thrown when the Client quits a Server.
 *
 * @author Ribesg
 */
public class ClientQuitServerEvent extends Event {

   private final Server server;
   private final String reason;

   public ClientQuitServerEvent(final Server server, final String reason) {
      this.server = server;
      this.reason = reason;
   }

   public Server getServer() {
      return server;
   }

   public String getReason() {
      return reason;
   }
}
