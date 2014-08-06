package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;

/**
 * Executed when the Client receive a Private Message.
 *
 * @author Ribesg
 */
public class PrivateMessageEvent extends Event {

   private final Server server;
   private final Source from;
   private final String message;

   public PrivateMessageEvent(final Server server, final Source from, final String message) {
      this.server = server;
      this.from = from;
      this.message = message;
   }

   public Server getServer() {
      return server;
   }

   public Source getFrom() {
      return from;
   }

   public String getMessage() {
      return message;
   }
}
