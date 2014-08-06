package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Executed when a User successfully joins a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#JOIN} command
 * from the Server with a User set as Prefix.
 * <p>
 * Important Note: If you need to interact with the list of users, please use the
 * {@link fr.ribesg.alix.api.Channel#updateUsers(Runnable...)}.
 *
 * @author Ribesg
 */
public class UserJoinChannelEvent extends Event {

   private final Source  user;
   private final Channel channel;

   public UserJoinChannelEvent(final Source user, final Channel channel) {
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
