package fr.ribesg.alix.api.event.connection;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.event.Event;

/**
 * Called after the Client has joined a Server.
 * It's safe to call other commands in this event, for example join channels.
 *
 * @author Ribesg
 */
public class ServerJoinedEvent extends Event {

	private final Server joinedServer;

	public ServerJoinedEvent(final Client client, final Server joinedServer) {
		super(client);
		this.joinedServer = joinedServer;
	}

	/**
	 * Gets the Server this Client has just joined.
	 *
	 * @return the Server this Client just joined
	 */
	public Server getJoinedServer() {
		return this.joinedServer;
	}
}
