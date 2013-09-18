package fr.ribesg.alix.api;
/**
 * Represents something on the IRC network that can receive messages.
 *
 * @author Ribesg
 */
public abstract class Receiver {

	private final Server server;
	private final String name;

	protected Receiver(final Server server, final String name) {
		this.server = server;
		this.name = name;
	}

	/** @return The Server this channel belongs to. */
	public Server getServer() {
		return this.server;
	}

	/**
	 * @return The name of this Channel on its Server with type char,
	 *         is unique per-Server
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sends a message to this Receiver.
	 * <p/>
	 * Will call
	 * {@link Server#sendRaw(String, fr.ribesg.alix.api.enums.Command, String, String...)}
	 * with appropriate parameters
	 *
	 * @param message The message to send
	 */
	public void sendMessage(String message) {
		// TODO Implement stuff
	}

	/**
	 * Sends an action to this Receiver.
	 * An Action is when somebody uses the command /me is a teapot
	 * <p/>
	 * Will call
	 * {@link Server#sendRaw(String, fr.ribesg.alix.api.enums.Command, String, String...)}
	 * with appropriate parameters
	 *
	 * @param action The Action text to send
	 */
	public void sendAction(String action) {
		// TODO Implement stuff
	}
}
