package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

public class JoinMessage extends Message {

	public JoinMessage(final String channelName) {
		super(null, Command.JOIN.name(), null, channelName);
	}
}
