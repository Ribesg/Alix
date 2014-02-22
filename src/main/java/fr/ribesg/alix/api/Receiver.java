package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgMessage;

/**
 * Represents something on the IRC network that can receive messages.
 *
 * @author Ribesg
 */
public abstract class Receiver {

	private final Server       server;
	private final String       name;
	private final ReceiverType type;

	protected Receiver(final Server server, final String name, final ReceiverType type) {
		this.server = server;
		this.name = name;
		this.type = type;
	}

	/**
	 * @return The Server this channel belongs to.
	 */
	public Server getServer() {
		return this.server;
	}

	/**
	 * @return The name of this Channel on its Server with type char,
	 * is unique per-Server
	 */
	public String getName() {
		return this.name;
	}

	public ReceiverType getType() {
		return this.type;
	}

	/**
	 * Sends a message to this Receiver.
	 *
	 * @param message Tthe message to send
	 */
	public void sendMessage(final String message) {
		server.send(new PrivMsgMessage(this.getName(), message));
	}

	/**
	 * Sends an action to this Receiver.
	 * An Action is when somebody uses the command /me is a teapot
	 *
	 * @param action the Action text to send
	 */
	public void sendAction(final String action) {
		// TODO Implement stuff
	}
}
