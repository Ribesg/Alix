package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a TOPIC IRC Packet.
 */
public class TopicIrcPacket extends IrcPacket {

	/**
	 * Just get TOPIC.
	 */
	public TopicIrcPacket(final String channelName) {
		super(null, Command.TOPIC.name(), null, channelName);
	}

	/**
	 * Change TOPIC.
	 */
	public TopicIrcPacket(final String channelName, final String newTopic) {
		super(null, Command.TOPIC.name(), newTopic, channelName);
	}
}
