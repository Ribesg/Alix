package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;

/**
 * Executed once a User quits a Server or get kicked from a Server.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#QUIT} command with another
 * name than the Client's name as prefix.
 *
 * @author Ribesg
 */
public class UserQuitServerEvent extends Event {

   private final Server server;
   private final Source user;
   private final String reason;

   public UserQuitServerEvent(final Server server, final Source user, final String reason) {
      this.server = server;
      this.user = user;
      this.reason = reason;
   }

   public Server getServer() {
      return server;
   }

   public Source getUser() {
      return user;
   }

   public String getReason() {
      return reason;
   }
}
