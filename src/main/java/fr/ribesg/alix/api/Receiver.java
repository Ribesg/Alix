package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgMessage;

/**
 * Represents an entity that can receive Messages on a Server.
 *
 * @author Ribesg
 */
public abstract class Receiver {

	/**
	 * The Server this Receiver belongs to
	 */
	protected final Server server;

	/**
	 * The name of this Receiver
	 */
	protected final String name;

	/**
	 * Receiver constructor.
	 *
	 * @param server the Server this Receiver belongs to
	 * @param name   the name of the Receiver
	 */
	protected Receiver(final Server server, final String name) {
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
	 * @return the name of this Receiver
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
		this.server.send(new PrivMsgMessage(this.getName(), message));
	}

}
