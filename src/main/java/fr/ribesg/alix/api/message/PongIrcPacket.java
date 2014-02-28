package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PONG IRC Packet.
 */
public class PongIrcPacket extends IrcPacket {

	public PongIrcPacket(final String value) {
		super(null, Command.PONG.name(), value);
	}
}
