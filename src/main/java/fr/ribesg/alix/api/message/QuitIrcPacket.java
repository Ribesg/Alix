package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a QUIT IRC Packet.
 */
public class QuitIrcPacket extends IrcPacket {

	private final String reason;

	public QuitIrcPacket() {
		this(null);
	}

	public QuitIrcPacket(final String reason) {
		super(null, Command.QUIT.name(), reason);
		this.reason = reason;
	}

	/**
	 * @return true if this Quit packet holds a reason, false otherwise
	 */
	public boolean hasReason() {
		return this.reason != null;
	}

	/**
	 * @return the reason hold by this Quit packet if any, null otherwise
	 */
	public String getReason() {
		return this.reason;
	}
}
