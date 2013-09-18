package fr.ribesg.alix.api.event.connection;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.event.Event;

/**
 * Called after the Client has joined a Channel.
 * It's safe to call other commands in this event, for example speak in this
 * channel.
 *
 * @author Ribesg
 */
public class ChannelJoinedEvent extends Event {

	private final Channel joinedChannel;

	public ChannelJoinedEvent(final Client client, final Channel joinedChannel) {
		super(client);
		this.joinedChannel = joinedChannel;
	}

	/**
	 * Gets the Channel this Client has just joined.
	 *
	 * @return the Channel this Client just joined
	 */
	public Channel getJoinedChannel() {
		return this.joinedChannel;
	}
}
