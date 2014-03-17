package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PONG IRC Packet.
 */
public class PingIrcPacket extends IrcPacket {

	private final String value;

	public PingIrcPacket(final String value) {
		super(null, Command.PING.name(), value);
		this.value = value;
	}

	/**
	 * @return this Ping packet value
	 */
	public String getValue() {
		return this.value;
	}
}
