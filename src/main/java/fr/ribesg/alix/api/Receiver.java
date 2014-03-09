package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgIrcPacket;

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
	 * Sends one or multiple messages to this Receiver.
	 *
	 * @param messages messages to send
	 */
	public void sendMessage(final String... messages) {
		for (final String message : messages) {
			this.server.send(new PrivMsgIrcPacket(this.getName(), message));
		}
	}

}
