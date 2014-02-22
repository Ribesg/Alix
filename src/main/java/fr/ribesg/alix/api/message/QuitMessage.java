package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a QUIT Message.
 */
public class QuitMessage extends Message {

	public QuitMessage(final String reason) {
		super(null, Command.QUIT.name(), reason);
	}
}
