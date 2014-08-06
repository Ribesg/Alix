package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;

/**
 * Thrown once the Client parts a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive an
 * echo of the {@link fr.ribesg.alix.api.enums.Command#PART} command
 * from the Server that confirms that the Client has successfully parted
 * the Channel.
 *
 * @author Ribesg
 * @see ClientKickedFromChannelEvent Client kick event
 */
public class ClientPartChannelEvent extends Event {

   private final Channel channel;

   public ClientPartChannelEvent(final Channel channel) {
      this.channel = channel;
   }

   public Channel getChannel() {
      return channel;
   }
}
