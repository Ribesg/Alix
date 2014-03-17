package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PRIVMSG IRC Packet.
 */
public class PrivMsgIrcPacket extends IrcPacket {

	private final String receiver;
	private final String message;

	public PrivMsgIrcPacket(final String receiver, final String message) {
		super(null, Command.PRIVMSG.name(), message, receiver);
		this.receiver = receiver;
		this.message = message;
	}

	/**
	 * @return this PrivMsg packet receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @return this PrivMsg packet message
	 */
	public String getMessage() {
		return message;
	}
}
