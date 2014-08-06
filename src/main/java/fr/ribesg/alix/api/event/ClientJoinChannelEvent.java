package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Channel;

/**
 * Thrown once the Client successfully joins a Channel.
 * <p>
 * To be more precise, this is triggered once the Client receive an
 * echo of the {@link fr.ribesg.alix.api.enums.Command#JOIN} command
 * from the Server that confirms that the Client has successfully joined
 * the Channel.
 * <p>
 * Important Note: If you need to interact with the list of users, please use the
 * {@link Channel#updateUsers(Runnable...)} method.
 *
 * @author Ribesg
 */
public class ClientJoinChannelEvent extends Event {

   private final Channel channel;

   public ClientJoinChannelEvent(final Channel channel) {
      this.channel = channel;
   }

   public Channel getChannel() {
      return channel;
   }
}
