package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PONG Message.
 */
public class PongMessage extends Message {

	public PongMessage(final String value) {
		super(null, Command.PONG.name(), value);
	}
}
