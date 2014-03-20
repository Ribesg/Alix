package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;

/**
 * Represents a Received Packet as the Server from which it comes
 * and the String packet.
 */
public class ReceivedPacket {

	private final Server source;
	private final String packet;

	public ReceivedPacket(Server source, String packet) {
		this.source = source;
		this.packet = packet;
	}

	public Server getSource() {
		return source;
	}

	public String getPacket() {
		return packet;
	}
}
