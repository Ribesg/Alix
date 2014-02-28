package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a USER IRC Packet.
 */
public class UserIrcPacket extends IrcPacket {

	public UserIrcPacket(final String userName) {
		this(userName, null);
	}

	public UserIrcPacket(final String userName, final String realName) {
		super(null, Command.USER.name(), realName != null ? realName : userName, userName, ".", ".");
	}
}
