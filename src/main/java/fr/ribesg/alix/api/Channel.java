package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgMessage;

/**
 * Represents an IRC Channel.
 *
 * @author Ribesg
 */
public class Channel {

	private final Server server;
	private final String name;

	protected Channel(final Server server, final String name) {
		this.server = server;
		this.name = name;
	}

	/**
	 * @return the Server this Receiver belongs to
	 */
	public Server getServer() {
		return this.server;
	}

	/**
	 * @return the name of this Receiver on its Server,
	 * is unique per-Server
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sends a message to this Receiver.
	 *
	 * @param message the message to send
	 */
	public void sendMessage(final String message) {
		server.send(new PrivMsgMessage(this.getName(), message));
	}
}
