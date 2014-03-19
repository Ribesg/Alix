package fr.ribesg.alix.api;

/**
 * Represents the Source of a Message, most of the time
 * written in the Prefix part of the Message.
 * <p>
 * This can be either a Server or a User.
 */
public class Source extends Receiver {

	/**
	 * The User name of this Source, if any
	 */
	private final String userName;

	/**
	 * The Host name of this Source, if any
	 */
	private final String hostName;

	/**
	 * If this Source is a User
	 */
	private final boolean isUser;

	/**
	 * User Source constructor.
	 *
	 * @param server   the Server this Source belongs to
	 * @param name     the name of the Source
	 * @param userName the User name of the Source
	 * @param hostName the Host name of the Source
	 */
	public Source(final Server server, final String name, final String userName, final String hostName) {
		this(server, name, userName, hostName, true);
	}

	/**
	 * Server Source constructor.
	 *
	 * @param server   the Server this Source belongs to
	 * @param hostName the Host name / Name of the Source
	 */
	public Source(final Server server, final String hostName) {
		this(server, hostName, null, hostName, false);
	}

	/**
	 * <strong>Please do not use this constructor</strong>
	 * <p>
	 * Mainly user for testing the Prefix parser.
	 *
	 * @param server   the Server this Source belongs to
	 * @param name     the name of the Source
	 * @param userName the User name of the Source
	 * @param hostName the Host name of the Source
	 * @param isUser   if this Source is a User
	 */
	public Source(final Server server, final String name, final String userName, final String hostName, final boolean isUser) {
		super(server, name);
		this.userName = userName;
		this.hostName = hostName;
		this.isUser = isUser;
	}

	/**
	 * If this Source if a User, this will return the User name of this User
	 * if it's known, null if it isn't.
	 * If this Source is not a User, this will return null.
	 *
	 * @return the User name of this Source if any, null otherwise
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * If this Source is a User, this will return the Host name of this User
	 * if it's known, null if it isn't.
	 * If this Source is not a User, this will return the exact same thing
	 * than {@link #getName()}.
	 *
	 * @return the Host name of this Source if any, null otherwise
	 */
	public String getHostName() {
		return this.hostName;
	}

	/**
	 * @return true if this Source is a User, false otherwise
	 */
	public boolean isUser() {
		return this.isUser;
	}

	/**
	 * @return true if this Source is a Server, false otherwise
	 */
	public boolean isServer() {
		return !this.isUser;
	}
}
