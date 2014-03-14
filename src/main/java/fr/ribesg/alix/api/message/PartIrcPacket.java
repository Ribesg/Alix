package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PART IRC Packet.
 */
public class PartIrcPacket extends IrcPacket {

	/**
	 * Main constructor.
	 *
	 * @param channelName the Channel name
	 */
	public PartIrcPacket(final String channelName) {
		super(null, Command.PART.name(), null, channelName);
	}

	/**
	 * Password-protected Channel constructor.
	 *
	 * @param channelName the Channel name
	 * @param password    the password for this Channel
	 */
	public PartIrcPacket(final String channelName, final String password) {
		super(null, Command.PART.name(), null, channelName, password);
	}
}
