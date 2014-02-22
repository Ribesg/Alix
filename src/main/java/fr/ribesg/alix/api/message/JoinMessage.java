package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a JOIN Message.
 */
public class JoinMessage extends Message {

	/**
	 * Main constructor.
	 *
	 * @param channelName the Channel name
	 */
	public JoinMessage(final String channelName) {
		super(null, Command.JOIN.name(), null, channelName);
	}

	/**
	 * Password-protected Channel constructor.
	 *
	 * @param channelName the Channel name
	 * @param password    the password for this Channel
	 */
	public JoinMessage(final String channelName, final String password) {
		super(null, Command.JOIN.name(), null, channelName, password);
	}
}
