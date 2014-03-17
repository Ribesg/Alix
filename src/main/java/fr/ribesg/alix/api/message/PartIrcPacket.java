package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PART IRC Packet.
 */
public class PartIrcPacket extends IrcPacket {

	private final String channelName;

	/**
	 * Main constructor.
	 *
	 * @param channelName the Channel name
	 */
	public PartIrcPacket(final String channelName) {
		super(null, Command.PART.name(), null, channelName);
		this.channelName = channelName;
	}

	/**
	 * @return this Part packet Channel name
	 */
	public String getChannelName() {
		return this.channelName;
	}
}
