package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a NAMES IRC Packet.
 */
public class NamesIrcPacket extends IrcPacket {

	public NamesIrcPacket(final String channelName) {
		super(null, Command.NAMES.name(), null, channelName);
	}
}
