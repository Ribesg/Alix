package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Server;

/**
 * Executed once the Client gets kicked from a Server.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#QUIT} command with the
 * Client's name as prefix.
 *
 * @author Ribesg
 */
public class ClientKickedFromServerEvent extends Event {

   private final Server server;
   private final String reason;

   public ClientKickedFromServerEvent(final Server server, String reason) {
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
