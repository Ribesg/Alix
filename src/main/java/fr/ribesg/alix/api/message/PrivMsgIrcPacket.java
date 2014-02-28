package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PRIVMSG IRC Packet.
 */
public class PrivMsgIrcPacket extends IrcPacket {

	public PrivMsgIrcPacket(final String receiver, final String message) {
		super(null, Command.PRIVMSG.name(), message, receiver);
	}
}
