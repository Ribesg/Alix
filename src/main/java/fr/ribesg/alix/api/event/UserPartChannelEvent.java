package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Executed once a User parts a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#PART} command
 * from the Server with a User set as Prefix.
 *
 * @author Ribesg
 */
public class UserPartChannelEvent extends Event {

   private final Source  user;
   private final Channel channel;

   public UserPartChannelEvent(final Source user, final Channel channel) {
      this.user = user;
      this.channel = channel;
   }

   public Source getUser() {
      return user;
   }

   public Channel getChannel() {
      return channel;
   }
}
