package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Server;

/**
 * Executed once the Client loose the connection to the Server.
 * <p>
 * To be more precise, this is triggered if the Client doesn't receive a
 * {@link fr.ribesg.alix.api.enums.Command#PONG} command within 5 seconds
 * after sending a {@link fr.ribesg.alix.api.enums.Command#PING} command
 * to this Server.
 *
 * @author Ribesg
 */
public class ClientLostConnectionEvent extends Event {

   private final Server server;

   public ClientLostConnectionEvent(final Server server) {
      this.server = server;
   }

   public Server getServer() {
      return server;
   }
}
