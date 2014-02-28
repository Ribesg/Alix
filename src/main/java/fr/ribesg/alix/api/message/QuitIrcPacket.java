package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a QUIT IRC Packet.
 */
public class QuitIrcPacket extends IrcPacket {

	public QuitIrcPacket(final String reason) {
		super(null, Command.QUIT.name(), reason);
	}
}
