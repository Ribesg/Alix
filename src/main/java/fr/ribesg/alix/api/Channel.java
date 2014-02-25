package fr.ribesg.alix.api;
/**
 * Represents an IRC Channel.
 *
 * @author Ribesg
 */
public class Channel extends Receiver {

	/**
	 * The password of this Channel, if any
	 */
	private final String password;

	/**
	 * Channel constructor.
	 *
	 * @param server the Server this Channel belongs to
	 * @param name   the name of the Channel, in the format #foo
	 */
	public Channel(final Server server, final String name) {
		super(server, name);
		this.password = null;
	}

	/**
	 * Password-protected Channel constructor.
	 *
	 * @param server   the Server this Channel belongs to
	 * @param name     the name of the Channel, in the format #foo
	 * @param password the password of the Channel
	 */
	public Channel(final Server server, final String name, final String password) {
		super(server, name);
		this.password = password;
	}

	/**
	 * @return true if the Channel has a known password, false otherwise
	 */
	public boolean hasPassword() {
		return this.password != null;
	}

	/**
	 * @return the password of this Channel, if any
	 */
	public String getPassword() {
		return this.password;
	}
}
