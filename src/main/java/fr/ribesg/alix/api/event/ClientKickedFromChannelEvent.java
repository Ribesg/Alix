package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Executed once the Client gets kicked from a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive a
 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with the
 * Client's name as second parameter.
 *
 * @author Ribesg
 */
public class ClientKickedFromChannelEvent extends Event {

   private final Channel channel;
   private final Source  kicker;
   private final String  reason;

   public ClientKickedFromChannelEvent(final Channel channel, final Source kicker, final String reason) {
      this.channel = channel;
      this.kicker = kicker;
      this.reason = reason;
   }

   public Channel getChannel() {
      return channel;
   }

   public Source getKicker() {
      return kicker;
   }

   public String getReason() {
      return reason;
   }
}
