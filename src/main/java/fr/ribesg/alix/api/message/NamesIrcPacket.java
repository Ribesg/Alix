package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a NAMES IRC Packet.
 */
public class NamesIrcPacket extends IrcPacket {

	private final String channelName;

	public NamesIrcPacket(final String channelName) {
		super(null, Command.NAMES.name(), null, channelName);
		this.channelName = channelName;
	}

	/**
	 * @return this Names packet Channel name
	 */
	public String getChannelName() {
		return this.channelName;
	}
}
