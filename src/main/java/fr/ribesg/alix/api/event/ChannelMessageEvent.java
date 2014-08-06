package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Executed when the Client sees a message sent in a Channel.
 * <p>
 * Important Note: If you need to interact with the list of users, please use the
 * {@link fr.ribesg.alix.api.Channel#updateUsers(Runnable...)}.
 *
 * @author Ribesg
 */
public class ChannelMessageEvent extends Event {

   private final Channel channel;
   private final Source  user;
   private final String  message;

   public ChannelMessageEvent(Channel channel, Source user, String message) {
      this.channel = channel;
      this.user = user;
      this.message = message;
   }

   public Channel getChannel() {
      return channel;
   }

   public Source getUser() {
      return user;
   }

   public String getMessage() {
      return message;
   }
}
