package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a USER IRC Packet.
 */
public class UserIrcPacket extends IrcPacket {

	private final String userName;
	private final String realName;

	public UserIrcPacket(final String userName) {
		this(userName, null);
	}

	public UserIrcPacket(final String userName, final String realName) {
		super(null, Command.USER.name(), realName != null ? realName : userName, userName, ".", ".");
		this.userName = userName;
		this.realName = realName;
	}

	/**
	 * @return this User packet User name
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @return true if this User packet holds a Real name, false otherwise
	 */
	public boolean hasRealName() {
		return this.realName != null;
	}

	/**
	 * @return this User packet Real name if any, null otherwise
	 */
	public String getRealName() {
		return this.realName;
	}
}
