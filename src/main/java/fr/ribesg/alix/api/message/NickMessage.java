package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

public class NickMessage extends Message {

	public NickMessage(final String name) {
		super(null, Command.NICK.name(), null, name);
	}
}
