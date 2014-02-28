package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a JOIN IRC Packet.
 */
public class JoinIrcPacket extends IrcPacket {

	/**
	 * Main constructor.
	 *
	 * @param channelName the Channel name
	 */
	public JoinIrcPacket(final String channelName) {
		super(null, Command.JOIN.name(), null, channelName);
	}

	/**
	 * Password-protected Channel constructor.
	 *
	 * @param channelName the Channel name
	 * @param password    the password for this Channel
	 */
	public JoinIrcPacket(final String channelName, final String password) {
		super(null, Command.JOIN.name(), null, channelName, password);
	}
}
