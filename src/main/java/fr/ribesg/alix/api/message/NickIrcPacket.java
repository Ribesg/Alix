package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a NICK IRC Packet.
 */
public class NickIrcPacket extends IrcPacket {

	public NickIrcPacket(final String name) {
		super(null, Command.NICK.name(), null, name);
	}
}
