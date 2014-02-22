package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgMessage;

/**
 * Represents an IRC Channel.
 *
 * @author Ribesg
 */
public class Channel {

	/**
	 * The Server this Channel belongs to
	 */
	private final Server server;

	/**
	 * The name of this Channel
	 */
	private final String name;

	/**
	 * The password of this Channel, if any
	 */
	private final String password;

	/**
	 * Main constructor.
	 *
	 * @param server the Server this Channel belongs to
	 * @param name   the name of the Channel, in the format #foo
	 */
	public Channel(final Server server, final String name) {
		this(server, name, null);
	}

	/**
	 * Password-protected Channel constructor.
	 *
	 * @param server   the Server this Channel belongs to
	 * @param name     the name of the Channel, in the format #foo
	 * @param password the password of the Channel
	 */
	public Channel(final Server server, final String name, final String password) {
		this.server = server;
		this.name = name;
		this.password = password;
	}

	/**
	 * @return the Server this Channel belongs to
	 */
	public Server getServer() {
		return this.server;
	}

	/**
	 * @return the name of this Channel on its Server,
	 * is unique per-Server
	 */
	public String getName() {
		return this.name;
	}

	public boolean hasPassword() {
		return this.password != null;
	}

	/**
	 * @return the password of this Channel, if any
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sends a message to this Channel.
	 *
	 * @param message the message to send
	 */
	public void sendMessage(final String message) {
		this.server.send(new PrivMsgMessage(this.getName(), message));
	}
}
