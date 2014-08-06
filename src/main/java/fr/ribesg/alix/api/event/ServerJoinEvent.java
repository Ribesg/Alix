package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Server;

/**
 * Thrown once the Client successfully connects to a Server.
 * <p>
 * To be more precise, this is triggered once the Client receive
 * the welcome message
 * ({@link fr.ribesg.alix.api.enums.Reply#RPL_WELCOME}) from the Server.
 * At this point the Client has not joined any Channel yet.
 *
 * @author Ribesg
 * @see ClientJoinChannelEvent Doing stuff on Channel join
 */
public class ServerJoinEvent extends Event {

   private final Server server;

   public ServerJoinEvent(final Server server) {
      this.server = server;
   }

   public Server getServer() {
      return server;
   }
}
