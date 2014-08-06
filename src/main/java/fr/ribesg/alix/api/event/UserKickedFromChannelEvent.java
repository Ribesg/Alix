package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Executed once a User gets kicked from a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with another
 * name than the Client's name as second parameter.
 *
 * @author Ribesg
 */
public class UserKickedFromChannelEvent extends Event {

   private final Channel channel;
   private final Source  user;
   private final String  reason;

   public UserKickedFromChannelEvent(final Channel channel, final Source user, final String reason) {
      this.channel = channel;
      this.user = user;
      this.reason = reason;
   }

   public Channel getChannel() {
      return channel;
   }

   public Source getUser() {
      return user;
   }

   public String getReason() {
      return reason;
   }
}
