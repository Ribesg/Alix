package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

public class UserMessage extends Message {

	public UserMessage(final String userName) {
		this(userName, null);
	}

	public UserMessage(final String userName, final String realName) {
		super(null, Command.USER.name(), realName != null ? realName : userName, userName, ".", ".");
	}
}
