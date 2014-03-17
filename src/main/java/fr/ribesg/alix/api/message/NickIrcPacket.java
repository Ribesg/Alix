package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a NICK IRC Packet.
 */
public class NickIrcPacket extends IrcPacket {

	private final String newName;

	public NickIrcPacket(final String name) {
		super(null, Command.NICK.name(), null, name);
		this.newName = name;
	}

	/**
	 * @return this Nick packet new name
	 */
	public String getNewName() {
		return this.newName;
	}
}
